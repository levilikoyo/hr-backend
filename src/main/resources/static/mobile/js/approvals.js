/* =========================================================
   EMS-L Mobile - Approvals
   Full clean file
   Path: src/main/resources/static/mobile/js/approvals.js
   ========================================================= */

(function () {
    "use strict";

    let approvalCurrentUser = null;
    let approvalOrganization = "";
    let approvalUserRole = "";
    let pendingRequests = [];

    document.addEventListener("DOMContentLoaded", async function () {
        try {
            approvalCurrentUser = getLoggedInUserSafe();

            if (!approvalCurrentUser) {
                window.location.href = "login.html";
                return;
            }

            approvalOrganization = getOrganizationSafe(approvalCurrentUser);
            approvalUserRole = getRoleSafe(approvalCurrentUser);

            console.log("Approval current user:", approvalCurrentUser);
            console.log("Approval organization:", approvalOrganization);
            console.log("Approval user role:", approvalUserRole);

            if (!approvalOrganization) {
                alert("Organization not found. Please login again.");
                window.location.href = "login.html";
                return;
            }

            if (!approvalUserRole) {
                alert("User role not found. Please login again.");
                window.location.href = "login.html";
                return;
            }

            displayUserInfo();
            bindEvents();

            await loadPendingApprovals();

        } catch (error) {
            console.error("Approvals initialization error:", error);
            showError(error.message || "Failed to initialize approvals page.");
        }
    });

    /* =========================================================
       Events
       ========================================================= */

    function bindEvents() {
        const backBtn = findElement(
            ["backBtn", "btnBack", "backButton"],
            ["Back", "Retour"]
        );

        if (backBtn) {
            backBtn.addEventListener("click", function () {
                window.location.href = "index.html";
            });
        }

        const refreshBtn = findElement(
            ["refreshBtn", "btnRefresh", "refreshButton"],
            ["Refresh", "Actualiser"]
        );

        if (refreshBtn) {
            refreshBtn.addEventListener("click", async function () {
                await loadPendingApprovals();
            });
        }
    }

    /* =========================================================
       Load pending approvals
       ========================================================= */

    async function loadPendingApprovals() {
        setLoading(true);

        try {
            const role = normalizeRole(approvalUserRole);
            let requests = [];

            try {
                const url =
                    `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(approvalOrganization)}/${encodeURIComponent(role)}`;

                const data = await fetchJson(url);
                requests = toArray(data);

                console.log("Pending approvals from role endpoint:", requests);

            } catch (error) {
                console.warn("Pending approval role endpoint failed:", error);
            }

            /*
               ADMIN fallback:
               If ADMIN receives no result from role endpoint,
               load all organization requests and filter pending approval statuses.
            */
            if ((!requests || requests.length === 0) && role === "ADMIN") {
                try {
                    const url =
                        `${BASE_URL}/api/needs-requests/organization/${encodeURIComponent(approvalOrganization)}`;

                    const data = await fetchJson(url);
                    const allRequests = toArray(data);

                    requests = allRequests.filter(function (request) {
                        return isPendingForAnyApproval(request);
                    });

                    console.log("Pending approvals from ADMIN fallback:", requests);

                } catch (error) {
                    console.warn("ADMIN fallback failed:", error);
                }
            }

            pendingRequests = requests || [];

            renderPendingApprovals(pendingRequests);
            updatePendingCount(pendingRequests.length);

        } catch (error) {
            console.error("Load pending approvals error:", error);
            showError(`Failed to load pending approvals: ${error.message}`);
            renderPendingApprovals([]);
            updatePendingCount(0);
        } finally {
            setLoading(false);
        }
    }

    /* =========================================================
       Render approvals
       ========================================================= */

    function renderPendingApprovals(requests) {
        const container = findElement(
            [
                "pendingRequestsContainer",
                "approvalsContainer",
                "requestsContainer",
                "approvalList",
                "pendingList"
            ],
            [
                "Pending requests"
            ]
        );

        if (!container) {
            console.warn("Approval container not found in HTML.");
            return;
        }

        container.innerHTML = "";

        if (!requests || requests.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <h3>No pending requests</h3>
                    <p>There is no request waiting for your approval.</p>
                </div>
            `;
            return;
        }

        requests.forEach(function (request) {
            container.appendChild(createApprovalCard(request));
        });
    }

    function createApprovalCard(request) {
        const card = document.createElement("div");
        card.className = "approval-card";

        const requestId = request.id;
        const requestNo = safeText(request.requestNo || request.request_no || `REQ-${requestId}`);
        const title = safeText(request.title || "Needs Request");
        const status = safeText(request.status || "");
        const currentLevel = safeText(request.currentApprovalLevel || request.current_approval_level || "");
        const requester = safeText(
            request.requestedBy ||
            request.requested_by ||
            request.requesterName ||
            request.requester_name ||
            request.createdBy ||
            request.created_by ||
            ""
        );

        const department = safeText(request.department || "");
        const fundCode = safeText(request.fundCode || request.fund_code || "");
        const currencyCode = safeText(request.currencyCode || request.currency_code || "");
        const glAccountNo = safeText(
            request.glAccountNo ||
            request.gl_account_no ||
            request.glAccountCode ||
            request.gl_account_code ||
            ""
        );

        const requestDate = safeText(request.requestDate || request.request_date || "");
        const priority = safeText(request.priority || "");
        const description = safeText(request.description || "");

        card.innerHTML = `
            <div class="approval-card-header">
                <div>
                    <h3>${title}</h3>
                    <p class="muted">${requestNo}</p>
                </div>
                <span class="status-badge">${status}</span>
            </div>

            <div class="approval-card-body">
                ${description ? `<p>${description}</p>` : ""}

                <div class="info-grid">
                    <div>
                        <span class="label">Requester</span>
                        <strong>${requester || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">Date</span>
                        <strong>${formatDate(requestDate)}</strong>
                    </div>

                    <div>
                        <span class="label">Department</span>
                        <strong>${department || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">Fund</span>
                        <strong>${fundCode || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">Currency</span>
                        <strong>${currencyCode || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">G/L Account</span>
                        <strong>${glAccountNo || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">Approval Level</span>
                        <strong>${currentLevel || "-"}</strong>
                    </div>

                    <div>
                        <span class="label">Priority</span>
                        <strong>${priority || "-"}</strong>
                    </div>
                </div>
            </div>

            <div class="approval-card-actions">
                <button type="button" class="btn-secondary" data-action="view">
                    View
                </button>

                <button type="button" class="btn-danger" data-action="reject">
                    Reject
                </button>

                <button type="button" class="btn-primary" data-action="approve">
                    Approve
                </button>
            </div>
        `;

        const viewBtn = card.querySelector('[data-action="view"]');
        const rejectBtn = card.querySelector('[data-action="reject"]');
        const approveBtn = card.querySelector('[data-action="approve"]');

        if (viewBtn) {
            viewBtn.addEventListener("click", function () {
                showRequestDetails(request);
            });
        }

        if (rejectBtn) {
            rejectBtn.addEventListener("click", async function () {
                await rejectRequest(request);
            });
        }

        if (approveBtn) {
            approveBtn.addEventListener("click", async function () {
                await approveRequest(request);
            });
        }

        return card;
    }

    /* =========================================================
       Approval actions
       Backend expects:
       PUT /api/needs-requests/{id}/approve?approvedBy=...&role=...
       PUT /api/needs-requests/{id}/reject?rejectedBy=...&role=...&reason=...
       ========================================================= */

    async function approveRequest(request) {
        const requestId = request.id;

        if (!requestId) {
            showError("Invalid request.");
            return;
        }

        const confirmed = await confirmAction(
            "Approve request",
            "Do you want to approve this request?"
        );

        if (!confirmed) {
            return;
        }

        setLoading(true);

        try {
            const approvedBy = getUserDisplayName();
            const approvalRole = getApprovalRoleForRequest(request);

            console.log("Approving request with role:", approvalRole);

            if (!approvalRole) {
                throw new Error("Approval role not found for this request.");
            }

            const url =
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/approve` +
                `?approvedBy=${encodeURIComponent(approvedBy)}` +
                `&role=${encodeURIComponent(approvalRole)}`;

            await putRequest(url);

            showSuccess("Request approved successfully.");

            await loadPendingApprovals();

        } catch (error) {
            console.error("Approve request error:", error);
            showError(`Failed to approve request: ${error.message}`);
        } finally {
            setLoading(false);
        }
    }

    async function rejectRequest(request) {
        const requestId = request.id;

        if (!requestId) {
            showError("Invalid request.");
            return;
        }

        const reason = await promptRejectReason();

        if (reason === null) {
            return;
        }

        if (!String(reason).trim()) {
            showError("Please enter rejection reason.");
            return;
        }

        setLoading(true);

        try {
            const rejectedBy = getUserDisplayName();
            const approvalRole = getApprovalRoleForRequest(request);

            console.log("Rejecting request with role:", approvalRole);

            if (!approvalRole) {
                throw new Error("Approval role not found for this request.");
            }

            const url =
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/reject` +
                `?rejectedBy=${encodeURIComponent(rejectedBy)}` +
                `&role=${encodeURIComponent(approvalRole)}` +
                `&reason=${encodeURIComponent(reason)}`;

            await putRequest(url);

            showSuccess("Request rejected successfully.");

            await loadPendingApprovals();

        } catch (error) {
            console.error("Reject request error:", error);
            showError(`Failed to reject request: ${error.message}`);
        } finally {
            setLoading(false);
        }
    }

    async function putRequest(url) {
        console.log("PUT:", url);

        const response = await fetch(url, {
            method: "PUT",
            headers: {
                "Accept": "application/json"
            }
        });

        const text = await response.text();

        console.log("PUT response:", response.status, text);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status} - ${text}`);
        }

        if (!text) {
            return {};
        }

        try {
            return JSON.parse(text);
        } catch (error) {
            return {};
        }
    }

    function getApprovalRoleForRequest(request) {
        const userRole = normalizeRole(approvalUserRole);

        /*
           Normal real workflow:
           HOD approves HOD level,
           FINANCE approves Finance level,
           DIRECTOR approves Director level.
        */
        if (userRole === "HOD" || userRole === "FINANCE" || userRole === "DIRECTOR") {
            return userRole;
        }

        /*
           ADMIN testing mode:
           Send the role expected by the current request level.
        */
        const currentLevel = normalizeRole(
            request.currentApprovalLevel ||
            request.current_approval_level ||
            ""
        );

        if (currentLevel === "HOD") {
            return "HOD";
        }

        if (currentLevel === "FINANCE") {
            return "FINANCE";
        }

        if (currentLevel === "DIRECTOR") {
            return "DIRECTOR";
        }

        const status = normalizeRole(request.status || "");

        if (status.includes("HOD")) {
            return "HOD";
        }

        if (status.includes("FINANCE")) {
            return "FINANCE";
        }

        if (status.includes("DIRECTOR")) {
            return "DIRECTOR";
        }

        return userRole;
    }

    /* =========================================================
       Details
       ========================================================= */

    function showRequestDetails(request) {
        const dimensionValues = parseJsonSafe(request.dimensionValues || request.dimension_values);
        const items = request.items || request.requestItems || [];

        let dimensionHtml = "";

        Object.keys(dimensionValues).forEach(function (key) {
            const value = dimensionValues[key];

            if (!value) {
                return;
            }

            dimensionHtml += `
                <div>
                    <span class="label">${safeText(key)}</span>
                    <strong>${safeText(value)}</strong>
                </div>
            `;
        });

        let itemsHtml = "";

        if (Array.isArray(items) && items.length > 0) {
            items.forEach(function (item) {
                const itemName = safeText(item.itemName || item.item_name || item.description || "Item");
                const quantity = safeText(item.quantity || "");
                const totalAmount = safeText(item.totalAmount || item.total_amount || item.totalCost || "");

                itemsHtml += `
                    <div class="detail-item">
                        <strong>${itemName}</strong>
                        <span>Qty: ${quantity}</span>
                        <span>Total: ${totalAmount}</span>
                    </div>
                `;
            });
        }

        const message = `
            <div class="request-details">
                <p><strong>Request No:</strong> ${safeText(request.requestNo || request.request_no || "")}</p>
                <p><strong>Title:</strong> ${safeText(request.title || "")}</p>
                <p><strong>Status:</strong> ${safeText(request.status || "")}</p>
                <p><strong>Description:</strong> ${safeText(request.description || "")}</p>

                ${dimensionHtml ? `<h4>Dimensions</h4><div class="info-grid">${dimensionHtml}</div>` : ""}

                ${itemsHtml ? `<h4>Items</h4>${itemsHtml}` : ""}
            </div>
        `;

        if (window.MobileDialog && typeof window.MobileDialog.infoHtml === "function") {
            window.MobileDialog.infoHtml("Request details", message);
            return;
        }

        alert(stripHtml(message));
    }

    /* =========================================================
       Auth helpers
       ========================================================= */

    function getLoggedInUserSafe() {
        if (typeof window.getCurrentUser === "function") {
            return window.getCurrentUser();
        }

        const keys = [
            "ems_mobile_user",
            "currentUser",
            "mobileUser",
            "user"
        ];

        for (const key of keys) {
            const raw = localStorage.getItem(key);

            if (!raw) {
                continue;
            }

            try {
                const user = JSON.parse(raw);

                if (user && typeof user === "object") {
                    return user;
                }
            } catch (error) {
                console.warn("Invalid stored user:", key, error);
            }
        }

        return null;
    }

    function getOrganizationSafe(user) {
        if (typeof window.getCurrentOrganization === "function") {
            return window.getCurrentOrganization();
        }

        return firstValue(user, [
            "organization",
            "organisation",
            "organizationCode",
            "organisationCode",
            "company",
            "org"
        ]) || localStorage.getItem("ems_mobile_organization") || "";
    }

    function getRoleSafe(user) {
        if (typeof window.getCurrentUserRole === "function") {
            return window.getCurrentUserRole();
        }

        return normalizeRole(
            firstValue(user, [
                "role",
                "userRole",
                "user_role",
                "profile",
                "position"
            ])
        );
    }

    /* =========================================================
       General helpers
       ========================================================= */

    async function fetchJson(url) {
        console.log("Fetching:", url);

        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            cache: "no-store"
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(`HTTP ${response.status} - ${text}`);
        }

        if (!text) {
            return [];
        }

        try {
            return JSON.parse(text);
        } catch (error) {
            console.error("Invalid JSON:", text);
            throw new Error("Invalid JSON response from server.");
        }
    }

    function toArray(data) {
        if (Array.isArray(data)) {
            return data;
        }

        if (!data || typeof data !== "object") {
            return [];
        }

        if (Array.isArray(data.data)) {
            return data.data;
        }

        if (Array.isArray(data.content)) {
            return data.content;
        }

        if (Array.isArray(data.items)) {
            return data.items;
        }

        if (Array.isArray(data.result)) {
            return data.result;
        }

        if (Array.isArray(data.results)) {
            return data.results;
        }

        return [];
    }

    function normalizeRole(role) {
        return String(role || "")
            .trim()
            .toUpperCase()
            .replace(/\s+/g, "_");
    }

    function getUserDisplayName() {
        return (
            approvalCurrentUser.fullName ||
            approvalCurrentUser.full_name ||
            approvalCurrentUser.name ||
            approvalCurrentUser.username ||
            approvalCurrentUser.email ||
            "User"
        );
    }

    function isPendingForAnyApproval(request) {
        const status = normalizeRole(request.status || "");

        return (
            status === "PENDING_HOD" ||
            status === "PENDING_HOD_APPROVAL" ||
            status === "PENDING_FINANCE" ||
            status === "PENDING_FINANCE_APPROVAL" ||
            status === "PENDING_DIRECTOR" ||
            status === "PENDING_DIRECTOR_APPROVAL"
        );
    }

    function updatePendingCount(count) {
        const ids = [
            "pendingCount",
            "approvalCount",
            "requestsCount"
        ];

        ids.forEach(function (id) {
            const element = document.getElementById(id);

            if (element) {
                element.textContent = String(count || 0);
            }
        });

        const badges = document.querySelectorAll(".pending-count, .approval-count");

        badges.forEach(function (badge) {
            badge.textContent = String(count || 0);
        });
    }

    function displayUserInfo() {
        const name = getUserDisplayName();
        const role = approvalUserRole || "";

        setTextIfExists("currentUserName", name);
        setTextIfExists("userName", name);
        setTextIfExists("mobileUserName", name);

        setTextIfExists("currentUserRole", role);
        setTextIfExists("userRole", role);
        setTextIfExists("mobileUserRole", role);

        const possibleNameElements = document.querySelectorAll(".user-name, .profile-name");
        possibleNameElements.forEach(function (element) {
            element.textContent = name;
        });

        const possibleRoleElements = document.querySelectorAll(".user-role, .profile-role");
        possibleRoleElements.forEach(function (element) {
            element.textContent = role;
        });
    }

    function setTextIfExists(id, value) {
        const element = document.getElementById(id);

        if (element) {
            element.textContent = value || "";
        }
    }

    function setLoading(isLoading) {
        const loaders = document.querySelectorAll(".loading, .loader");

        loaders.forEach(function (loader) {
            loader.style.display = isLoading ? "block" : "none";
        });
    }

    async function confirmAction(title, message) {
        if (window.MobileDialog && typeof window.MobileDialog.confirm === "function") {
            return await window.MobileDialog.confirm(title, message);
        }

        return confirm(message);
    }

    async function promptRejectReason() {
        return prompt("Enter rejection reason:");
    }

    function showError(message) {
        if (window.MobileDialog && typeof window.MobileDialog.error === "function") {
            window.MobileDialog.error("Error", message);
            return;
        }

        alert(message);
    }

    function showSuccess(message) {
        if (window.MobileDialog && typeof window.MobileDialog.success === "function") {
            window.MobileDialog.success("Success", message);
            return;
        }

        alert(message);
    }

    function findElement(keys, labelTexts) {
        const keyList = Array.isArray(keys) ? keys : [];
        const labelList = Array.isArray(labelTexts) ? labelTexts : [];

        for (const key of keyList) {
            if (!key) {
                continue;
            }

            const byId = document.getElementById(key);

            if (byId) {
                return byId;
            }

            const byName = document.querySelector(`[name="${cssEscape(key)}"]`);

            if (byName) {
                return byName;
            }

            const byDataField = document.querySelector(`[data-field="${cssEscape(key)}"]`);

            if (byDataField) {
                return byDataField;
            }
        }

        for (const labelText of labelList) {
            const found = findElementByLabelText(labelText);

            if (found) {
                return found;
            }
        }

        return null;
    }

    function findElementByLabelText(labelText) {
        if (!labelText) {
            return null;
        }

        const labels = document.querySelectorAll("label");

        for (const label of labels) {
            const text = String(label.textContent || "").trim().toUpperCase();
            const expected = String(labelText || "").trim().toUpperCase();

            if (!text.includes(expected)) {
                continue;
            }

            const forId = label.getAttribute("for");

            if (forId) {
                const element = document.getElementById(forId);

                if (element) {
                    return element;
                }
            }

            const parent = label.parentElement;

            if (parent) {
                return parent;
            }
        }

        return null;
    }

    function parseJsonSafe(value) {
        if (!value) {
            return {};
        }

        if (typeof value === "object") {
            return value;
        }

        try {
            return JSON.parse(value);
        } catch (error) {
            return {};
        }
    }

    function firstValue(object, keys) {
        if (!object || typeof object !== "object") {
            return "";
        }

        for (const key of keys) {
            const value = object[key];

            if (value !== null && value !== undefined && String(value).trim() !== "") {
                return String(value).trim();
            }
        }

        return "";
    }

    function safeText(value) {
        return escapeHtml(value || "");
    }

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function stripHtml(value) {
        const div = document.createElement("div");
        div.innerHTML = value;
        return div.textContent || div.innerText || "";
    }

    function formatDate(value) {
        if (!value) {
            return "-";
        }

        try {
            return new Date(value).toLocaleDateString();
        } catch (error) {
            return value;
        }
    }

    function cssEscape(value) {
        if (window.CSS && typeof window.CSS.escape === "function") {
            return window.CSS.escape(value);
        }

        return String(value).replace(/"/g, '\\"');
    }

})();