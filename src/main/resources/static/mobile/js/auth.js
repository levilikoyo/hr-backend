const AUTH_USER_KEY = "mobileApprovalUser";

function saveCurrentUser(user) {
    if (!user) {
        return;
    }

    const normalizedUser = {
        id: user.id || null,
        organization: user.organization || "",
        fullName: user.fullName || "",
        email: user.email || "",
        phone: user.phone || "",
        role: user.role || user.userRole || "",
        active: user.active !== false
    };

    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(normalizedUser));
}

function getCurrentUser() {
    const rawUser = localStorage.getItem(AUTH_USER_KEY);

    if (!rawUser) {
        return null;
    }

    try {
        return JSON.parse(rawUser);
    } catch (error) {
        console.error(error);
        localStorage.removeItem(AUTH_USER_KEY);
        return null;
    }
}

function getCurrentOrganization() {
    const user = getCurrentUser();

    if (!user || !user.organization) {
        return "";
    }

    return user.organization;
}

function requireLogin() {
    const user = getCurrentUser();

    if (!user || !user.email) {
        window.location.href = "login.html";
        return null;
    }

    if (!user.organization) {
        localStorage.removeItem(AUTH_USER_KEY);
        window.location.href = "login.html";
        return null;
    }

    return user;
}

function requireRole(allowedRoles) {
    const user = requireLogin();

    if (!user) {
        return null;
    }

    const role = String(user.role || "").toUpperCase();

    if (!allowedRoles.includes(role)) {
        window.location.href = "index.html";
        return null;
    }

    return user;
}

function logoutUser() {
    localStorage.removeItem(AUTH_USER_KEY);
    window.location.href = "login.html";
}

function displayCurrentUser() {
    const user = getCurrentUser();

    if (!user) {
        return;
    }

    setText("currentUserName", user.fullName || "User");
    setText("currentUserRole", `${user.role || ""} - ${user.organization || ""}`);

    setText("dropdownUserName", user.fullName || "User");
    setText("dropdownUserEmail", user.email || "");
    setText("dropdownUserRole", `${user.role || ""} - ${user.organization || ""}`);
}

function toggleUserMenu() {
    const dropdown = document.getElementById("userDropdown");

    if (!dropdown) {
        return;
    }

    dropdown.classList.toggle("show");
}

document.addEventListener("click", function (event) {
    const userBox = document.querySelector(".user-info-box");
    const dropdown = document.getElementById("userDropdown");

    if (!dropdown || !userBox) {
        return;
    }

    if (!userBox.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.classList.remove("show");
    }
});

function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value;
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const pinCode = document.getElementById("pinCode").value.trim();

        if (!email || !pinCode) {
            await showMessageDialog(
                "Missing information",
                "Please enter your email and PIN code.",
                "warning"
            );
            return;
        }

        try {
            const response = await fetch(`${BASE_URL}/api/mobile-auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    pinCode: pinCode
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Login failed");
            }

            const data = await response.json();

            if (!data.organization) {
                throw new Error("Your user account has no organization assigned.");
            }

            saveCurrentUser(data);

            window.location.href = "index.html";

        } catch (error) {
            console.error(error);

            await showMessageDialog(
                "Login failed",
                error.message,
                "danger"
            );
        }
    });
});