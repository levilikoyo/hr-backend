const AUTH_USER_KEY = "mobileApprovalUser";

function saveCurrentUser(user) {
    sessionStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
}

function getCurrentUser() {
    const rawUser = sessionStorage.getItem(AUTH_USER_KEY);

    if (!rawUser) {
        return null;
    }

    try {
        return JSON.parse(rawUser);
    } catch (e) {
        sessionStorage.removeItem(AUTH_USER_KEY);
        return null;
    }
}

function requireLogin() {
    const user = getCurrentUser();

    if (!user) {
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

    if (!allowedRoles.includes(user.role)) {
        showMessageDialog(
            "Access denied",
            "You are not allowed to access this page.",
            "danger"
        ).then(() => {
            window.location.href = "index.html";
        });

        return null;
    }

    return user;
}

function logoutUser() {
    sessionStorage.removeItem(AUTH_USER_KEY);
    window.location.href = "login.html";
}

function displayCurrentUser() {
    const user = getCurrentUser();

    const userNameElement = document.getElementById("currentUserName");
    const userRoleElement = document.getElementById("currentUserRole");

    if (userNameElement && user) {
        userNameElement.textContent = user.fullName;
    }

    if (userRoleElement && user) {
        userRoleElement.textContent = user.role;
    }
}

function setLoginLoading(isLoading) {
    const button = document.querySelector("#loginForm button[type='submit']");

    if (!button) {
        return;
    }

    if (isLoading) {
        button.disabled = true;
        button.textContent = "Connexion...";
    } else {
        button.disabled = false;
        button.textContent = "Se connecter";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    if (!loginForm) {
        displayCurrentUser();
        return;
    }

    loginForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const pinCode = document.getElementById("pinCode").value.trim();

        if (!email) {
            await showMessageDialog(
                "Missing email",
                "Please enter your email.",
                "warning"
            );
            return;
        }

        if (!pinCode) {
            await showMessageDialog(
                "Missing PIN",
                "Please enter your PIN code.",
                "warning"
            );
            return;
        }

        const loginData = {
            organization: ORGANIZATION,
            email: email,
            pinCode: pinCode
        };

        try {
            setLoginLoading(true);

            const response = await fetch(`${BASE_URL}/api/mobile-auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(loginData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Login failed");
            }

            const user = await response.json();

            saveCurrentUser(user);

            await showMessageDialog(
                "Connected",
                "Welcome " + user.fullName + ".",
                "success"
            );

            window.location.href = "index.html";

        } catch (error) {
            console.error(error);

            await showMessageDialog(
                "Login failed",
                error.message,
                "danger"
            );

        } finally {
            setLoginLoading(false);
        }
    });
});