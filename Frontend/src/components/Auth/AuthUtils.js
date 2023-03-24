import {url} from "../../App";

export async function loginUser(credentials) {
    return fetch(`${url}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
}

export async function logout(){
    sessionStorage.removeItem("userId");
    sessionStorage.removeItem("token");
    window.location.reload();
}

export async function createUser(credentials) {
    return fetch(`${url}/users`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
}

export async function verify(userid, token) {
    if (!userid || !token) {
        return false;
    }

    let response = await fetch(`${url}/login/verify`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: JSON.parse(token),
            userId: JSON.parse(userid)
        })
    })
    return response.status === 200;
}

export async function verifyLogin() {
    let had = sessionStorage.getItem("userId") || sessionStorage.getItem("token");

    if (had && !await verify(sessionStorage.getItem("userId"), sessionStorage.getItem("token"))) {
        sessionStorage.removeItem("userId");
        sessionStorage.removeItem("token");
        window.location.reload();
    }
}