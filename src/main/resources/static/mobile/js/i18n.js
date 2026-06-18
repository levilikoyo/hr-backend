/* =========================================================
   EMS-L Mobile - Language helper
   ========================================================= */

(function () {
    "use strict";

    const STORAGE_KEY = "emsMobileLanguage";
    const SUPPORTED_LANGUAGES = ["en", "fr"];

    const translations = {
        en: {
            "app.name": "EMS-L Approval",
            "common.user": "User",
            "common.role": "Role",
            "common.logout": "Logout",
            "common.language": "Language",
            "common.english": "English",
            "common.french": "French",
            "common.back": "Back",
            "common.loading": "Loading...",
            "common.cancel": "Cancel",
            "common.submit": "Submit",
            "common.email": "Email",
            "common.pin": "PIN Code",
            "common.notifications": "Notifications",
            "login.title": "Login",
            "login.subtitle": "Mobile approval access",
            "login.emailPlaceholder": "Your email address",
            "login.pinPlaceholder": "Your PIN",
            "login.button": "Sign in",
            "login.loading": "Signing in...",
            "login.missingCredentials": "Please enter email and PIN.",
            "login.footer": "EMS-L Finance System · Mobile Approval",
            "home.title": "Mobile Approval",
            "home.subtitle": "Needs request",
            "home.myPending": "My pending",
            "home.approved": "Approved",
            "home.rejected": "Rejected",
            "home.toApprove": "To approve",
            "home.newRequest": "New request",
            "home.newRequestDesc": "Create a needs request",
            "home.approvals": "Approvals",
            "home.approvalsDesc": "Approve or reject requests",
            "home.myRequests": "My requests",
            "home.myRequestsDesc": "Track submitted requests",
            "home.notificationsDesc": "View alerts and updates",
            "needs.title": "Needs request",
            "needs.subtitle": "Create a new request",
            "needs.object": "Need title *",
            "needs.objectPlaceholder": "Example: Field fuel purchase",
            "needs.description": "General description",
            "needs.descriptionPlaceholder": "Describe the general need...",
            "needs.requestDate": "Requested date *",
            "needs.priority": "Priority",
            "needs.departmentApproval": "Department approval",
            "needs.addressedDepartment": "Addressed Department *",
            "needs.loadingDepartments": "Loading departments...",
            "needs.items": "Items requested",
            "needs.item": "Item",
            "needs.newItemSummary": "New item | Qty: 1 PCS | Total: 0.00",
            "needs.itemName": "Item name *",
            "needs.itemNamePlaceholder": "Example: Fuel",
            "needs.itemDescriptionPlaceholder": "Item details...",
            "needs.category": "Category",
            "needs.select": "Select",
            "needs.goods": "Goods",
            "needs.service": "Service",
            "needs.works": "Works",
            "needs.other": "Other",
            "needs.unit": "Unit",
            "needs.quantity": "Quantity *",
            "needs.unitPrice": "Unit Price",
            "needs.lineTotal": "Line total",
            "needs.addItem": "+ Add item",
            "needs.totalAmount": "Total amount:",
            "needs.budgetAccounting": "Budget and accounting information",
            "needs.budgetPlan": "Budget Plan",
            "needs.budgetPlanPlaceholder": "Optional budget plan",
            "needs.fund": "Fund *",
            "needs.loadingFunds": "Loading funds...",
            "needs.currency": "Currency *",
            "needs.loadingCurrencies": "Loading currencies...",
            "needs.glAccount": "G/L Account *",
            "needs.loadingGlAccounts": "Loading G/L accounts...",
            "needs.dimensions": "Dimensions",
            "needs.submit": "Submit Request",
            "needs.submitting": "Submitting...",
            "approvals.title": "Approvals",
            "approvals.subtitle": "Pending requests",
            "approvals.pendingRequests": "Pending requests",
            "approvals.loading": "Loading pending approvals...",
            "myRequests.title": "My requests",
            "myRequests.subtitle": "Request tracking",
            "myRequests.requests": "Requests",
            "myRequests.all": "All",
            "notifications.title": "Notifications",
            "notifications.subtitle": "Alerts and updates",
            "notifications.mine": "My notifications",
            "notifications.markAllRead": "Mark all as read",
            "auth.accessDenied": "Access denied",
            "auth.notAllowed": "You are not allowed to access this page.",
            "auth.loggedOut": "Logged out successfully"
        },
        fr: {
            "app.name": "EMS-L Approval",
            "common.user": "Utilisateur",
            "common.role": "Rôle",
            "common.logout": "Déconnexion",
            "common.language": "Langue",
            "common.english": "Anglais",
            "common.french": "Français",
            "common.back": "Retour",
            "common.loading": "Chargement...",
            "common.cancel": "Annuler",
            "common.submit": "Soumettre",
            "common.email": "Email",
            "common.pin": "Code PIN",
            "common.notifications": "Notifications",
            "login.title": "Connexion",
            "login.subtitle": "Accès mobile approval",
            "login.emailPlaceholder": "Votre adresse email",
            "login.pinPlaceholder": "Votre PIN",
            "login.button": "Se connecter",
            "login.loading": "Connexion...",
            "login.missingCredentials": "Veuillez saisir l'email et le PIN.",
            "login.footer": "EMS-L Finance System · Mobile Approval",
            "home.title": "Mobile Approval",
            "home.subtitle": "Expression de besoin",
            "home.myPending": "Mes attentes",
            "home.approved": "Approuvées",
            "home.rejected": "Rejetées",
            "home.toApprove": "À approuver",
            "home.newRequest": "Nouvelle demande",
            "home.newRequestDesc": "Créer une expression de besoin",
            "home.approvals": "Approbations",
            "home.approvalsDesc": "Valider ou rejeter les demandes",
            "home.myRequests": "Mes demandes",
            "home.myRequestsDesc": "Suivre les demandes soumises",
            "home.notificationsDesc": "Voir les alertes et mises à jour",
            "needs.title": "Expression de besoin",
            "needs.subtitle": "Créer une nouvelle demande",
            "needs.object": "Objet du besoin *",
            "needs.objectPlaceholder": "Ex: Achat carburant terrain",
            "needs.description": "Description générale",
            "needs.descriptionPlaceholder": "Décrivez le besoin général...",
            "needs.requestDate": "Date souhaitée *",
            "needs.priority": "Priorité",
            "needs.departmentApproval": "Approbation département",
            "needs.addressedDepartment": "Département adressé *",
            "needs.loadingDepartments": "Chargement des départements...",
            "needs.items": "Articles demandés",
            "needs.item": "Article",
            "needs.newItemSummary": "Nouvel article | Qté : 1 PCS | Total : 0.00",
            "needs.itemName": "Nom de l'article *",
            "needs.itemNamePlaceholder": "Ex: Carburant",
            "needs.itemDescriptionPlaceholder": "Détail de l'article...",
            "needs.category": "Catégorie",
            "needs.select": "Sélectionner",
            "needs.goods": "Biens",
            "needs.service": "Service",
            "needs.works": "Travaux",
            "needs.other": "Autre",
            "needs.unit": "Unité",
            "needs.quantity": "Quantité *",
            "needs.unitPrice": "Prix unitaire",
            "needs.lineTotal": "Total ligne",
            "needs.addItem": "+ Ajouter un article",
            "needs.totalAmount": "Montant total :",
            "needs.budgetAccounting": "Informations budgétaires et comptables",
            "needs.budgetPlan": "Plan budgétaire",
            "needs.budgetPlanPlaceholder": "Plan budgétaire optionnel",
            "needs.fund": "Fonds *",
            "needs.loadingFunds": "Chargement des fonds...",
            "needs.currency": "Devise *",
            "needs.loadingCurrencies": "Chargement des devises...",
            "needs.glAccount": "Compte G/L *",
            "needs.loadingGlAccounts": "Chargement des comptes G/L...",
            "needs.dimensions": "Dimensions",
            "needs.submit": "Soumettre la demande",
            "needs.submitting": "Soumission...",
            "approvals.title": "Approbations",
            "approvals.subtitle": "Demandes en attente",
            "approvals.pendingRequests": "Demandes en attente",
            "approvals.loading": "Chargement des approbations...",
            "myRequests.title": "Mes demandes",
            "myRequests.subtitle": "Suivi des demandes",
            "myRequests.requests": "Demandes",
            "myRequests.all": "Toutes",
            "notifications.title": "Notifications",
            "notifications.subtitle": "Alertes et mises à jour",
            "notifications.mine": "Mes notifications",
            "notifications.markAllRead": "Tout marquer comme lu",
            "auth.accessDenied": "Accès refusé",
            "auth.notAllowed": "Vous n'êtes pas autorisé à accéder à cette page.",
            "auth.loggedOut": "Déconnexion réussie"
        }
    };

    function getLanguage() {
        const stored = localStorage.getItem(STORAGE_KEY);

        if (SUPPORTED_LANGUAGES.includes(stored)) {
            return stored;
        }

        const browserLanguage = String(navigator.language || "en").slice(0, 2).toLowerCase();
        return SUPPORTED_LANGUAGES.includes(browserLanguage) ? browserLanguage : "en";
    }

    function setLanguage(language) {
        const nextLanguage = SUPPORTED_LANGUAGES.includes(language) ? language : "en";
        localStorage.setItem(STORAGE_KEY, nextLanguage);
        applyTranslations();
        window.dispatchEvent(new CustomEvent("ems-language-change", { detail: { language: nextLanguage } }));
    }

    function t(key, fallback) {
        const language = getLanguage();
        return translations[language][key] || translations.en[key] || fallback || key;
    }

    function applyTranslations(root) {
        const language = getLanguage();
        const scope = root || document;

        document.documentElement.lang = language;

        scope.querySelectorAll("[data-i18n]").forEach(function (element) {
            element.textContent = t(element.getAttribute("data-i18n"));
        });

        scope.querySelectorAll("[data-i18n-title]").forEach(function (element) {
            element.title = t(element.getAttribute("data-i18n-title"));
        });

        scope.querySelectorAll("[data-i18n-placeholder]").forEach(function (element) {
            element.placeholder = t(element.getAttribute("data-i18n-placeholder"));
        });

        scope.querySelectorAll("[data-i18n-aria-label]").forEach(function (element) {
            element.setAttribute("aria-label", t(element.getAttribute("data-i18n-aria-label")));
        });

        document.querySelectorAll("[data-language-select]").forEach(function (select) {
            select.value = language;
        });
    }

    function createLanguageSelector() {
        const wrapper = document.createElement("label");
        wrapper.className = "language-switcher";
        wrapper.innerHTML = `
            <span data-i18n="common.language">${t("common.language")}</span>
            <select data-language-select aria-label="${t("common.language")}">
                <option value="en">${t("common.english")}</option>
                <option value="fr">${t("common.french")}</option>
            </select>
        `;

        const select = wrapper.querySelector("select");
        select.value = getLanguage();
        select.addEventListener("change", function () {
            setLanguage(select.value);
        });

        return wrapper;
    }

    function ensureLanguageSelectors() {
        document.querySelectorAll("[data-language-switcher]").forEach(function (target) {
            if (!target.querySelector("[data-language-select]")) {
                target.appendChild(createLanguageSelector());
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        ensureLanguageSelectors();
        applyTranslations();
    });

    window.EMSI18n = {
        getLanguage: getLanguage,
        setLanguage: setLanguage,
        t: t,
        apply: applyTranslations,
        createLanguageSelector: createLanguageSelector,
        ensureLanguageSelectors: ensureLanguageSelectors
    };
})();
