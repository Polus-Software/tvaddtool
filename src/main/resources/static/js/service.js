var BASE_URL = window.location.origin + window.location.pathname;
var lookups = {};
var UserDetails = {};
var isLogin = false;

async function checkLoginDetails() {
	alert("test");
    const data = preparePersonObject();
    isLogin = await savePerson(data);
    if (!isLogin) {
        alert("login failed");
    } else {
        url = BASE_URL + '/success';
        window.open(url, '_self')
    }
}

function preparePersonObject() {
    const personDetails = {};
    personDetails.userName = (document.getElementById("loginUsername").value;
    personDetails.password = document.getElementById("loginPassword").value;
    return personDetails;
}

function savePerson(data) {
    return new Promise((resolve, reject) => {
        const http = new XMLHttpRequest();
        http.onreadystatechange = function () {
            if (this.readyState === 4 && this.status === 200) {
                resolve(JSON.parse(this.responseText));
            } else if (this.readyState === 4 && this.status !== 200) {
                reject('error');
            }
        };
        http.open('POST', BASE_URL + '/login', data);
        http.setRequestHeader('Content-Type', 'application/json');
        http.send(JSON.stringify(data));
    })
}

function getLookupData() {
    return new Promise((resolve, reject) => {
        const http = new XMLHttpRequest();
        http.onreadystatechange = function () {
            if (this.readyState === 4 && this.status === 200) {
                resolve(JSON.parse(this.responseText));
            } else if (this.readyState === 4 && this.status !== 200) {
                reject('error');
            }
        };
        http.open('GET', BASE_URL + '/getLookupData', true);
        http.setRequestHeader('Content-Type', 'application/json');
        http.send();
    });
}

async function initializeData() {
    lookups = await getLookupData();
    setDefaultValues();
    populateOrganization();
    populateUnits();
    toggleEnableDisableFormFields(true);
    createCaptcha();
}

function setDefaultValues() {
    if (isQueryParamExist()) {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const params = Object.fromEntries(urlSearchParams.entries());
        document.getElementById("fullName").value = params.fname;
        document.getElementById("emailAddress").value = params.email;
        document.getElementById("userName").value = params.name;
        document.getElementById("password").value = 'dummyPassword@123';
        document.getElementById("confirmPassword").value = 'dummyPassword@123';
        document.getElementById("riseAppend").classList.add('d-none');
    }
}

function populateOrganization() {
    const organizationList = lookups.organizationList;
    let organization = document.getElementById('organizationName');
    for (let i = 0; i < organizationList.length; i++) {
        organization.innerHTML = organization.innerHTML +
            '<option value="' + organizationList[i].organizationId + '">' + organizationList[i].organizationName + '</option>';
    }
}

function populateUnits() {
    const unitList = lookups.unitList;
    let unit = document.getElementById('fundingUnit');
    for (let i = 0; i < unitList.length; i++) {
        unit.innerHTML = unit.innerHTML +
            '<option value="' + unitList[i].unitNumber + '">' + unitList[i].unitName + '</option>';
    }
}

function toggleEnableDisableFormFields(status, queryParamStatus = true) {
    document.getElementById("fullName").disabled = queryParamStatus;
    document.getElementById("emailAddress").disabled = queryParamStatus;
    document.getElementById("userName").disabled = queryParamStatus;
    document.getElementById("password").disabled = queryParamStatus;
    document.getElementById("confirmPassword").disabled = queryParamStatus;
    document.getElementById("fundingUnit").disabled = status;
    document.getElementById("captchaTextBox").disabled = status;
    document.getElementById("termsCheck").disabled = status;
    document.getElementById("registerButton").disabled = status;
}

function onOrganizationSelect() {
    const OrgId = document.getElementById('organizationName').value;
    isPartneringOrg = lookups.organizationList.find((O) => O.organizationId === OrgId);
    isPartneringOrg = isPartneringOrg && isPartneringOrg.isPartneringOrganization == "Y" ? true : false;
    isQueryParam = isQueryParamExist();
    if (!isPartneringOrg) {
        document.getElementById('usernameInfoBanner').classList.remove('d-none');
        document.getElementById('riseAppend').classList.remove('d-none');
    }
    if (isPartneringOrg && !isQueryParam) {
        showModal();
    } else if (OrgId && isQueryParam) {
        toggleEnableDisableFormFields(false, true);
    } else if (OrgId && !isQueryParam) {
        toggleEnableDisableFormFields(false, false);
    }
}

function isQueryParamExist() {
    return window.location.search;
}

function validatePersonDetails() {
    clearErrors();
    let error = false;
    if (!document.getElementById("fundingUnit").value) {
        document.getElementById("fundingUnit").classList.add('is-invalid', 'd-block');
        error = true;
    }
    if (!document.getElementById("fullName").value) {
        document.getElementById("fullName").classList.add('is-invalid', 'd-block');
        error = true;
    }
    const email = document.getElementById("emailAddress").value;
    if (!email || validateEmail(email)) {
        document.getElementById("emailAddress").classList.add('is-invalid', 'd-block');
        error = true;
    }
    if (!document.getElementById("userName").value) {
        document.getElementById("userName").classList.add('is-invalid', 'd-block');
        error = true;
    }
    const password = document.getElementById("password").value;
    if (!password || validatePassWord(password)) {
        document.getElementById("password").classList.add('is-invalid', 'd-block');
        document.getElementById('passwordRule').classList.remove('d-none');
        error = true;
    }
    const confirmPassword = document.getElementById("confirmPassword").value;
    if (!confirmPassword) {
		document.getElementById("confirmPassword").classList.add("is-invalid", "d-block");
		error = true;
	}
    if (password != confirmPassword) {
		document.getElementById("confirmPassword").classList.add("is-invalid", "d-block");
		document.getElementById("confirmPasswordError").classList.remove("d-none");
		error = true;
	}
    if (!document.getElementById("captchaTextBox").value) {
        document.getElementById("captchaTextBox").classList.add('is-invalid', 'd-block');
        error = true;
    }
    if (!document.getElementById("termsCheck").checked) {
        document.getElementById("termsCheckError").classList.remove("d-none");
        error = true;
    }
    return !error;

}

function clearErrors() {
    let elements = document.querySelectorAll(".is-invalid");
    elements.forEach((el) => el.classList.remove('is-invalid', 'd-block'));
    document.getElementById('passwordRule').classList.add('d-none');
    document.getElementById('userNameError').classList.add('d-none');
    document.getElementById('emailError').classList.add('d-none');
    document.getElementById('confirmPasswordError').classList.add('d-none');
    document.getElementById('termsCheckError').classList.add('d-none');
}

function validatePassWord(password) {
    var RE = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*-+?])[A-Za-z\\d~!@#$%^&*-+?]{8,}$");
    return !RE.test(password)
}

function validateEmail(email) {
    var RE = new RegExp("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
    return !RE.test(email)
}

function showModal() {
    $('#orgSelectWarningModal').modal('show');
}

function openExternalIDP() {
    window.open('https://ds.sgaf.org.sg/discovery/DS?entityID=https://fibiuat.ntu.edu.sg/shibboleth&return=https://fibiuat.ntu.edu.sg/Shibboleth.sso/Login?SAMLDS=1&target=ss%3Amem%3A8ad9530029a5755652d436fd21b543ef65c3cefca1ba01423f43c91030317e38&Ounit=mahesh', '_self')
}

function cancelAction() {
    document.getElementById('organizationName').value = '';
    document.getElementById('usernameInfoBanner').classList.add('d-none');
    document.getElementById('riseAppend').classList.add('d-none');
    toggleEnableDisableFormFields(true, true);
}

function limitUserName(element) {
    if (element.target.value.length > 10) {
        element.target.value = element.target.value.substr(0, 10);
    }
}
