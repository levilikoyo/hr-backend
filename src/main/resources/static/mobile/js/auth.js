/* =========================================================
   EMS-L Mobile - Auth Helper
   Full clean file
   Path: src/main/resources/static/mobile/js/auth.js
   ========================================================= */

(function () {
    "use strict";

    const USER_STORAGE_KEYS = [
        "currentUser",
        "emsCurrentUser",
        "mobileUser",
        "loggedUser",
        "user"
    ];

    const TOKEN_STORAGE_KEYS = [
        "authToken",
        "emsAuthToken",
        "mobileToken",
        "token",
        "accessToken"
    ];

    document.addEventListener("DOMContentLoaded", function () {
        displayCurrentUser();
        ensureUserDropdown();
        bindUserDropdown();
        applyLanguage();
    });

    /* =========================================================
       CURRENT USER
       ========================================================= */

    function getCurrentUser() {
        for (const key of USER_STORAGE_KEYS) {
            const rawValue =
                localStorage.getItem(key) ||
                sessionStorage.getItem(key);

            if (!rawValue) {
                continue;
            }

            try {
                const parsedUser = JSON.parse(rawValue);

                if (parsedUser && typeof parsedUser === "object") {
                    return normalizeUser(parsedUser);
                }
            } catch (error) {
                console.warn("Invalid stored user for key:", key, error);
            }
        }

        return null;
    }

    function saveCurrentUser(user, token) {
        if (!user || typeof user !== "object") {
            return;
        }

        const normalizedUser = normalizeUser(user);

        localStorage.setItem("currentUser", JSON.stringify(normalizedUser));
        localStorage.setItem("emsCurrentUser", JSON.stringify(normalizedUser));
        localStorage.setItem("mobileUser", JSON.stringify(normalizedUser));

        if (token) {
            localStorage.setItem("authToken", token);
            localStorage.setItem("emsAuthToken", token);
            localStorage.setItem("mobileToken", token);
        }
    }

    function normalizeUser(user) {
        const normalized = Object.assign({}, user);

        normalized.id =
            firstExistingValue(user, [
                "id",
                "userId",
                "employeeId",
                "staffId"
            ]) || "";

        normalized.fullName =
            firstExistingValue(user, [
                "fullName",
                "fullname",
                "name",
                "employeeName",
                "staffName",
                "username",
                "userName"
            ]) || "";

        normalized.name = normalized.fullName;

        normalized.email =
            firstExistingValue(user, [
                "email",
                "mail",
                "userEmail",
                "employeeEmail"
            ]) || "";

        normalized.role =
            normalizeRole(
                firstExistingValue(user, [
                    "role",
                    "userRole",
                    "profile",
                    "accessRole",
                    "approvalRole"
                ]) || ""
            );

        normalized.organization =
            firstExistingValue(user, [
                "organization",
                "organisation",
                "org",
                "company",
                "companyCode",
                "organizationCode",
                "organisationCode"
            ]) || "";

        normalized.department =
            firstExistingValue(user, [
                "department",
                "departement",
                "costCenter",
                "cost_center"
            ]) || "";

        return normalized;
    }

    function requireLogin() {
        const user = getCurrentUser();

        if (!user) {
            redirectToLogin();
            throw new Error("User not logged in.");
        }

        return user;
    }

    function isLoggedIn() {
        return getCurrentUser() !== null;
    }

    function getCurrentToken() {
        for (const key of TOKEN_STORAGE_KEYS) {
            const token =
                localStorage.getItem(key) ||
                sessionStorage.getItem(key);

            if (token) {
                return token;
            }
        }

        return "";
    }

    function getCurrentOrganization() {
        const user = getCurrentUser();

        if (!user) {
            return "";
        }

        return (
            user.organization ||
            user.organisation ||
            user.org ||
            user.company ||
            user.companyCode ||
            user.organizationCode ||
            user.organisationCode ||
            ""
        );
    }

    function getCurrentUserRole() {
        const user = getCurrentUser();

        if (!user) {
            return "";
        }

        return normalizeRole(
            user.role ||
            user.userRole ||
            user.profile ||
            user.accessRole ||
            user.approvalRole ||
            ""
        );
    }

    function requireRole(allowedRoles) {
        const user = requireLogin();
        const role = getCurrentUserRole();

        const allowed = Array.isArray(allowedRoles)
            ? allowedRoles.map(normalizeRole)
            : [normalizeRole(allowedRoles)];

        if (!allowed.includes(role) && role !== "ADMIN") {
            showAuthError(translate("auth.notAllowed", "You are not allowed to access this page."));
            redirectToHome();
            throw new Error("Unauthorized role.");
        }

        return user;
    }

    /* =========================================================
       DISPLAY USER
       ========================================================= */

    function displayCurrentUser() {
        const user = getCurrentUser();

        if (!user) {
            return;
        }

        const name =
            user.fullName ||
            user.name ||
            user.username ||
            user.email ||
            "User";

        const role =
            user.role ||
            user.userRole ||
            "USER";

        const email =
            user.email ||
            "";

        setText("currentUserName", name);
        setText("currentUserRole", role);

        setText("dropdownUserName", name);
        setText("dropdownUserEmail", email);
        setText("dropdownUserRole", role);

        setText("userName", name);
        setText("userRole", role);
        setText("userEmail", email);
    }

    function setText(id, value) {
        const element = document.getElementById(id);

        if (element) {
            element.textContent = value || "";
        }
    }

    /* =========================================================
       USER DROPDOWN
       ========================================================= */

    function ensureUserDropdown() {
        const user = getCurrentUser();

        if (!user) {
            return;
        }

        let userInfoBox = document.getElementById("userInfoBox");

        if (!userInfoBox) {
            const possibleUserName =
                document.getElementById("currentUserName") ||
                document.getElementById("userName");

            if (possibleUserName) {
                userInfoBox = possibleUserName.closest(".user-info-box") ||
                    possibleUserName.closest(".user-box") ||
                    possibleUserName.parentElement;
            }
        }

        if (!userInfoBox) {
            return;
        }

        userInfoBox.id = "userInfoBox";
        userInfoBox.style.cursor = "pointer";
        userInfoBox.style.position = "relative";

        let userDropdown = document.getElementById("userDropdown");

        if (!userDropdown) {
            userDropdown = document.createElement("div");
            userDropdown.id = "userDropdown";
            userDropdown.className = "user-dropdown";

            userInfoBox.parentElement.appendChild(userDropdown);
        }

        const name =
            user.fullName ||
            user.name ||
            user.username ||
            user.email ||
            "User";

        const role =
            user.role ||
            user.userRole ||
            "USER";

        const email =
            user.email ||
            "";

        userDropdown.innerHTML = `
            <strong id="dropdownUserName">${escapeHtml(name)}</strong>
            <span id="dropdownUserEmail">${escapeHtml(email)}</span>
            <small id="dropdownUserRole">${escapeHtml(role)}</small>
            <div data-language-switcher></div>
            <button type="button" id="logoutBtn" data-i18n="common.logout">${translate("common.logout", "Logout")}</button>
        `;

        forceDropdownBaseStyle(userDropdown);
        applyLanguage(userDropdown);

        const logoutBtn = document.getElementById("logoutBtn");

        if (logoutBtn) {
            logoutBtn.onclick = function (event) {
                event.preventDefault();
                event.stopPropagation();
                logout();
            };
        }
    }

    function bindUserDropdown() {
        document.addEventListener("click", function (event) {
            const clickedUserBox =
                event.target.closest("#userInfoBox") ||
                event.target.closest(".user-info-box") ||
                event.target.closest(".user-box") ||
                event.target.closest("#currentUserName") ||
                event.target.closest(".user-name");

            const clickedDropdown = event.target.closest("#userDropdown");

            if (clickedDropdown) {
                event.stopPropagation();
                return;
            }

            if (clickedUserBox) {
                event.preventDefault();
                event.stopPropagation();

                toggleUserDropdown();
                return;
            }

            hideUserDropdown();
        });

        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                hideUserDropdown();
            }
        });
    }

    function toggleUserDropdown() {
        const dropdown = document.getElementById("userDropdown");

        if (!dropdown) {
            ensureUserDropdown();
            return;
        }

        const isOpen =
            dropdown.classList.contains("show") ||
            dropdown.style.display === "block";

        if (isOpen) {
            hideUserDropdown();
        } else {
            showUserDropdown();
        }
    }

    function showUserDropdown() {
        const dropdown = document.getElementById("userDropdown");

        if (!dropdown) {
            return;
        }

        dropdown.classList.add("show");
        dropdown.style.display = "block";
        dropdown.style.position = "absolute";
        dropdown.style.right = "0";
        dropdown.style.top = "48px";
        dropdown.style.zIndex = "9999";
    }

    function hideUserDropdown() {
        const dropdown = document.getElementById("userDropdown");

        if (!dropdown) {
            return;
        }

        dropdown.classList.remove("show");
        dropdown.style.display = "none";
    }

    function forceDropdownBaseStyle(dropdown) {
        dropdown.style.display = "none";
        dropdown.style.position = "absolute";
        dropdown.style.right = "0";
        dropdown.style.top = "48px";
        dropdown.style.width = "230px";
        dropdown.style.background = "#ffffff";
        dropdown.style.border = "1px solid #e5e7eb";
        dropdown.style.borderRadius = "18px";
        dropdown.style.boxShadow = "0 18px 45px rgba(15, 23, 42, 0.18)";
        dropdown.style.padding = "14px";
        dropdown.style.zIndex = "9999";
    }

    /* =========================================================
       LOGOUT
       ========================================================= */

    function logout() {
        clearAuth();

        if (window.MobileDialog && typeof window.MobileDialog.successToast === "function") {
            window.MobileDialog.successToast(translate("auth.loggedOut", "Logged out successfully"));
        }

        setTimeout(function () {
            window.location.href = "login.html";
        }, 250);
    }

    function clearAuth() {
        USER_STORAGE_KEYS.forEach(function (key) {
            localStorage.removeItem(key);
            sessionStorage.removeItem(key);
        });

        TOKEN_STORAGE_KEYS.forEach(function (key) {
            localStorage.removeItem(key);
            sessionStorage.removeItem(key);
        });

        localStorage.removeItem("currentOrganization");
        localStorage.removeItem("organisation");
        localStorage.removeItem("organization");

        sessionStorage.removeItem("currentOrganization");
        sessionStorage.removeItem("organisation");
        sessionStorage.removeItem("organization");
    }

    function redirectToLogin() {
        const currentPage = getCurrentPageName();

        if (currentPage === "login.html") {
            return;
        }

        window.location.href = "login.html";
    }

    function redirectToHome() {
        window.location.href = "index.html";
    }

    function getCurrentPageName() {
        const path = window.location.pathname || "";
        const parts = path.split("/");
        return parts[parts.length - 1] || "index.html";
    }

    /* =========================================================
       AUTH FETCH
       ========================================================= */

    async function authFetch(url, options) {
        const token = getCurrentToken();

        const finalOptions = options || {};
        finalOptions.headers = finalOptions.headers || {};

        if (token) {
            finalOptions.headers.Authorization = `Bearer ${token}`;
        }

        if (!finalOptions.headers.Accept) {
            finalOptions.headers.Accept = "application/json";
        }

        return fetch(url, finalOptions);
    }

    /* =========================================================
       HELPERS
       ========================================================= */

    function normalizeRole(role) {
        return String(role || "")
            .trim()
            .toUpperCase()
            .replace(/\s+/g, "_");
    }

    function firstExistingValue(object, keys) {
        if (!object || typeof object !== "object") {
            return "";
        }

        for (const key of keys) {
            if (!Object.prototype.hasOwnProperty.call(object, key)) {
                continue;
            }

            const value = object[key];

            if (value !== null && value !== undefined && String(value).trim() !== "") {
                return String(value).trim();
            }
        }

        return "";
    }

    function showAuthError(message) {
        if (window.MobileDialog && typeof window.MobileDialog.error === "function") {
            window.MobileDialog.error(translate("auth.accessDenied", "Access denied"), message);
            return;
        }

        alert(message);
    }

    function translate(key, fallback) {
        if (window.EMSI18n && typeof window.EMSI18n.t === "function") {
            return window.EMSI18n.t(key, fallback);
        }

        return fallback || key;
    }

    function applyLanguage(root) {
        if (!window.EMSI18n) {
            return;
        }

        if (typeof window.EMSI18n.ensureLanguageSelectors === "function") {
            window.EMSI18n.ensureLanguageSelectors();
        }

        if (typeof window.EMSI18n.apply === "function") {
            window.EMSI18n.apply(root);
        }
    }

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    /* =========================================================
       EXPOSE GLOBALLY
       ========================================================= */

    window.saveCurrentUser = saveCurrentUser;
    window.getCurrentUser = getCurrentUser;
    window.getCurrentToken = getCurrentToken;
    window.getCurrentOrganization = getCurrentOrganization;
    window.getCurrentUserRole = getCurrentUserRole;

    window.requireLogin = requireLogin;
    window.requireRole = requireRole;
    window.isLoggedIn = isLoggedIn;

    window.logout = logout;
    window.logoutUser = logout;
    window.clearAuth = clearAuth;

    window.displayCurrentUser = displayCurrentUser;
    window.toggleUserMenu = toggleUserDropdown;
    window.authFetch = authFetch;
})();
