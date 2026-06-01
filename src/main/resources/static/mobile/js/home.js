let currentUser = null;

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    prepareHomeForRole();
    loadDashboardBadges();
});

function prepareHomeForRole() {
    const approvalMenuCard = document.getElementById("approvalMenuCard");
    const pendingApprovalCard = document.getElementById("pendingApprovalCard");

    const canApprove = ["HOD", "FINANCE", "DIRECTOR", "ADMIN"]
            .includes(String(currentUser.role || "").toUpperCase());

    if (!canApprove) {
        if (approvalMenuCard) {
            approvalMenuCard.style.display = "none";
        }

        if (pendingApprovalCard) {
            pendingApprovalCard.style.display = "none";
        }
    }
}

async function loadDashboardBadges() {
    try {
        const allRequests = await fetchAllRequests();
        const myRequests = getMyRequests(allRequests);

        updateMyRequestBadges(myRequests);

        if (["HOD", "FINANCE", "DIRECTOR", "ADMIN"]
                .includes(String(currentUser.role || "").toUpperCase())) {

            const pendingApprovals = await fetchPendingApprovalsForUser();
            updateApprovalBadges(pendingApprovals.length);
        }

        const unreadNotifications = await fetchUnreadNotificationCount();
        updateNotificationBadges(unreadNotifications);

    } catch (error) {
        console.error(error);

        await showMessageDialog(
                "Dashboard error",
                "Failed to load dashboard badges: " + error.message,
                "danger"
        );
    }
}

async function fetchAllRequests() {
    const organization = getCurrentOrganization();

    const response = await fetch(
            `${BASE_URL}/api/needs-requests/organization/${encodeURIComponent(organization)}`
    );

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error("HTTP " + response.status + " - " + errorText);
    }

    return await response.json();
}

async function fetchPendingApprovalsForUser() {
    const organization = getCurrentOrganization();

    const response = await fetch(
            `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(organization)}/${encodeURIComponent(currentUser.role)}`
    );

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error("HTTP " + response.status + " - " + errorText);
    }

    return await response.json();
}

async function fetchUnreadNotificationCount() {
    const organization = getCurrentOrganization();

    const response = await fetch(
            `${BASE_URL}/api/mobile-notifications/unread-count?organization=${encodeURIComponent(organization)}&email=${encodeURIComponent(currentUser.email)}&role=${encodeURIComponent(currentUser.role)}`
    );

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error("HTTP " + response.status + " - " + errorText);
    }

    return await response.json();
}

function getMyRequests(allRequests) {
    if (!Array.isArray(allRequests)) {
        return [];
    }

    if (String(currentUser.role || "").toUpperCase() === "REQUESTER") {
        return allRequests.filter(request => {
            return request.requesterEmail === currentUser.email
                    || request.requestedBy === currentUser.fullName;
        });
    }

    return allRequests;
}

function updateMyRequestBadges(myRequests) {
    const myPending = myRequests.filter(request => {
        return request.status === "PENDING_HOD_APPROVAL"
                || request.status === "PENDING_FINANCE_REVIEW"
                || request.status === "PENDING_DIRECTOR_APPROVAL";
    }).length;

    const approved = myRequests.filter(request => request.status === "APPROVED").length;
    const rejected = myRequests.filter(request => request.status === "REJECTED").length;

    setText("myPendingCount", myPending);
    setText("approvedCount", approved);
    setText("rejectedCount", rejected);
    setText("myRequestsMenuBadge", myPending);
}

function updateApprovalBadges(count) {
    setText("pendingApprovalCount", count);
    setText("approvalMenuBadge", count);

    const badge = document.getElementById("approvalMenuBadge");

    if (badge) {
        badge.style.display = count > 0 ? "inline-flex" : "none";
    }
}

function updateNotificationBadges(count) {
    setText("notificationUnreadCount", count);
    setText("notificationMenuBadge", count);

    const badge = document.getElementById("notificationMenuBadge");

    if (badge) {
        badge.style.display = count > 0 ? "inline-flex" : "none";
    }
}

function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value;
    }
}