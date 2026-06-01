let currentUser = null;

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    loadNotifications();
});

async function loadNotifications() {
    const list = document.getElementById("notificationList");
    const count = document.getElementById("notificationCount");
    const organization = getCurrentOrganization();

    list.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">...</div>
            <h3>Loading notifications</h3>
            <p>Please wait...</p>
        </div>
    `;

    try {
        const response = await fetch(
                `${BASE_URL}/api/mobile-notifications/my-notifications?organization=${encodeURIComponent(organization)}&email=${encodeURIComponent(currentUser.email)}&role=${encodeURIComponent(currentUser.role)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const notifications = await response.json();

        count.textContent = notifications.length;
        list.innerHTML = "";

        if (!notifications || notifications.length === 0) {
            list.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">🔔</div>
                    <h3>No notifications</h3>
                    <p>You do not have any notification yet.</p>
                </div>
            `;
            return;
        }

        notifications.forEach(notification => {
            const card = document.createElement("div");
            card.className = notification.readStatus
                    ? "notification-card read"
                    : "notification-card unread";

            card.innerHTML = `
                <div class="notification-card-header">
                    <div>
                        <div class="notification-title">
                            ${escapeHtml(notification.title || "")}
                        </div>

                        <div class="notification-message">
                            ${escapeHtml(notification.message || "")}
                        </div>
                    </div>

                    ${notification.readStatus ? "" : `<span class="notification-dot"></span>`}
                </div>

                <div class="notification-meta">
                    <span>${escapeHtml(notification.notificationType || "")}</span>
                    <span>${formatDateTime(notification.createdAt)}</span>
                </div>

                ${notification.relatedRequestNo ? `
                    <div class="notification-request-no">
                        Request: ${escapeHtml(notification.relatedRequestNo)}
                    </div>
                ` : ""}

                <div class="notification-footer">
                    ${notification.readStatus ? `
                        <span class="read-label">Read</span>
                    ` : `
                        <button type="button" class="btn-secondary" onclick="markNotificationRead(${notification.id})">
                            Mark as read
                        </button>
                    `}

                    ${notification.relatedRequestId ? `
                        <button type="button" class="btn-primary small-btn" onclick="openRelatedRequest()">
                            Open requests
                        </button>
                    ` : ""}
                </div>
            `;

            list.appendChild(card);
        });

    } catch (error) {
        console.error(error);

        count.textContent = "0";

        list.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">!</div>
                <h3>Failed to load notifications</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

async function markNotificationRead(notificationId) {
    try {
        const response = await fetch(
                `${BASE_URL}/api/mobile-notifications/${notificationId}/mark-read`,
                {
                    method: "PUT"
                }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        await loadNotifications();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
                "Update failed",
                error.message,
                "danger"
        );
    }
}

async function markAllNotificationsRead() {
    const confirmed = await showConfirmDialog(
            "Mark all as read",
            "Do you want to mark all notifications as read?",
            "warning",
            "Mark all"
    );

    if (!confirmed) {
        return;
    }

    try {
        const organization = getCurrentOrganization();

        const response = await fetch(
                `${BASE_URL}/api/mobile-notifications/mark-all-read?organization=${encodeURIComponent(organization)}&email=${encodeURIComponent(currentUser.email)}&role=${encodeURIComponent(currentUser.role)}`,
                {
                    method: "PUT"
                }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        await showMessageDialog(
                "Updated",
                "All notifications marked as read.",
                "success"
        );

        await loadNotifications();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
                "Update failed",
                error.message,
                "danger"
        );
    }
}

function openRelatedRequest() {
    window.location.href = "my-requests.html";
}

function formatDateTime(value) {
    if (!value) {
        return "";
    }

    return String(value).replace("T", " ").substring(0, 16);
}

function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
}