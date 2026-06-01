/* =========================================================
   EMS-L Mobile - Authentication Helpers
   Full clean file
   Path: src/main/resources/static/mobile/js/auth.js
   ========================================================= */

const AUTH_USER_KEY = "ems_mobile_user";
const AUTH_TOKEN_KEY = "ems_mobile_token";
const AUTH_ORGANIZATION_KEY = "ems_mobile_organization";

/* =========================================================
   Save / read current user
   ========================================================= */

function saveCurrentUser(user) {
    if (!user || typeof user !== "object") {
        throw new Error("Invalid user data.");
    }

    const normalizedUser = normalizeUser(user);

    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(normalizedUser));

    if (normalizedUser.token) {
        localStorage.setItem(AUTH_TOKEN_KEY, normalizedUser.token);
    }

    if (normalizedUser.organization) {
        localStorage.setItem(AUTH_ORGANIZATION_KEY, normalizedUser.organization);
    }

    return normalizedUser;
}

function getCurrentUser() {
    const rawUser = localStorage.getItem(AUTH_USER_KEY);

    if (!rawUser) {
        return null;
    }

    try {
        const user = JSON.parse(rawUser);
        return normalizeUser(user);
    } catch (error) {
        console.error("Invalid stored user:", error);
        clearAuth();
        return null;
    }
}

function getCurrentToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY) || "";
}

function getCurrentOrganization() {
    const user = getCurrentUser();

    const organizationFromUser =
        user &&
        (
            user.organization ||
            user.organisation ||
            user.organizationCode ||
            user.organisationCode ||
            user.companyOrganization ||
            user.company ||
            user.org
        );

    if (organizationFromUser && String(organizationFromUser).trim() !== "") {
        return String(organizationFromUser).trim();
    }

    const organizationFromStorage = localStorage.getItem(AUTH_ORGANIZATION_KEY);

    if (organizationFromStorage && String(organizationFromStorage).trim() !== "") {
        return String(organizationFromStorage).trim();
    }

    return "";
}

function normalizeUser(user) {
    const organization =
        firstNonEmptyValue(user, [
            "organization",
            "organisation",
            "organizationCode",
            "organisationCode",
            "companyOrganization",
            "company",
            "org"
        ]);

    const fullName =
        firstNonEmptyValue(user, [
            "fullName",
            "fullname",
            "name",
            "names",
            "username",
            "userName",
            "email"
        ]);

    const role =
        firstNonEmptyValue(user, [
            "role",
            "userRole",
            "profile",
            "position"
        ]);

    return {
        ...user,
        id: user.id || user.userId || user.mobileUserId || null,
        fullName: fullName,
        username: user.username || user.userName || user.email || "",
        email: user.email || user.mail || "",
        role: role,
        organization: organization,
        token: user.token || user.accessToken || ""
    };
}

function firstNonEmptyValue(object, keys) {
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

/* =========================================================
   Login guard
   ========================================================= */

function requireLogin() {
    const user = getCurrentUser();

    if (!user) {
        window.location.href = "login.html";
        return null;
    }

    const organization = getCurrentOrganization();

    if (!organization) {
        clearAuth();
        alert("Organization not found. Please login again.");
        window.location.href = "login.html";
        return null;
    }

    return user;
}

function isLoggedIn() {
    return getCurrentUser() !== null;
}

function logout() {
    clearAuth();
    window.location.href = "login.html";
}

function clearAuth() {
    localStorage.removeItem(AUTH_USER_KEY);
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_ORGANIZATION_KEY);

    /*
       Clean older keys that may contain APN from old versions.
    */
    localStorage.removeItem("currentUser");
    localStorage.removeItem("mobileUser");
    localStorage.removeItem("user");
    localStorage.removeItem("organization");
    localStorage.removeItem("organisation");
    localStorage.removeItem("ORGANIZATION");
}

/* =========================================================
   Display current user
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

    const role = user.role || "";
    const organization = getCurrentOrganization();

    setTextIfExists("currentUserName", name);
    setTextIfExists("userName", name);
    setTextIfExists("mobileUserName", name);

    setTextIfExists("currentUserRole", role);
    setTextIfExists("userRole", role);
    setTextIfExists("mobileUserRole", role);

    setTextIfExists("currentOrganization", organization);
    setTextIfExists("organizationName", organization);
    setTextIfExists("mobileOrganization", organization);
}

function setTextIfExists(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value || "";
    }
}

/* =========================================================
   Authenticated fetch helper
   ========================================================= */

async function authFetch(url, options = {}) {
    const token = getCurrentToken();

    const headers = {
        ...(options.headers || {})
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    if (!headers.Accept) {
        headers.Accept = "application/json";
    }

    return fetch(url, {
        ...options,
        headers: headers
    });
}