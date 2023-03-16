import React, {useState} from "react"
import PropTypes from "prop-types";
import "./Auth.css"
import {createUser, loginUser} from "./AuthUtils";

const sha512 = require('js-sha512');

function hash(val) {
    let hash = sha512.update(val);
    return hash.hex();
}

export default function Login({setToken, setUserId}) {
    let [authMode, setAuthMode] = useState("signin")
    const [username, setUserName] = useState();
    const [password, setPassword] = useState();

    const [email, setEmail] = useState();
    const [name, setName] = useState();

    const [errorMessage, setErrorMessage] = useState();

    const changeAuthMode = () => {
        setErrorMessage(null)
        setAuthMode(authMode === "signin" ? "signup" : "signin")
    }

    async function doLogin() {
        const response = await loginUser({
            username: username.toLowerCase(),
            password: password
        });


        if (response.status === 200) {
            let json = await response.json();
            setToken(json.token);
            setUserId(json.userId);
            window.location.reload();
        } else {
            await setErrorMessage("Invalid username or password!");
        }
    }

    const handleCreateUserSubmit = async e => {
        e.preventDefault();
        const response = await createUser({
            username: username.toLowerCase(),
            email: email,
            name: name,
            password: password
        });

        if (response.status === 201) {
            await doLogin();
        } else {
            await setErrorMessage("Username already taken!");
        }
    }

    let submitFunc = authMode === "signin" ? async e => {
        e.preventDefault();
        await doLogin();
    } : async e => handleCreateUserSubmit();

    let nameField = <div className="input-container">
        <label>Full Name</label>
        <input
            type="text"
            placeholder="e.g Jane Doe"
            onChange={e => {
                setErrorMessage(null)
                setName(e.target.value)
            }}
        />
    </div>

    let usernameField = <div className="input-container">
        <label>Username</label>
        <input
            type="text"
            placeholder="Enter username"
            className="username"
            required
            onChange={e => {
                setErrorMessage(null)
                setUserName(e.target.value)
            }}
        />
    </div>

    let emailField = <div className="input-container">
        <label>Email address</label>
        <input
            type="email"
            placeholder="Email Address"
            onChange={e => {
                setErrorMessage(null)
                setEmail(e.target.value)
            }}
        />
    </div>

    let passwordField = <div className="input-container">
        <label>Password</label>
        <input
            type="password"
            className="password"
            placeholder="Enter password"
            required
            onChange={e => {
                setErrorMessage(null)
                setPassword(hash(e.target.value))
            }}
        />
    </div>

    let modeField = authMode === "signin" ?
        <div className="text-center">
            Not registered yet?{" "}
            <span className="link-primary" onClick={changeAuthMode}>
                                    Sign Up
                                </span>
        </div>
        :
        <div className="text-center">
            Already registered?{" "}
            <span className="link-primary" onClick={changeAuthMode}>
                                    Sign In
                                </span>
        </div>


    if (authMode === "signin") {
        nameField = null;
        emailField = null;
    }

    let element =
        <div className="login-form">
            <form className="form" onSubmit={submitFunc}>
                <h3 className="title">Sign In</h3>
                <h4 className="error">{errorMessage}</h4>

                {nameField}
                {usernameField}
                {emailField}
                {passwordField}

                <div className="button-container">
                    <button type="submit"> {authMode === "signin" ? "Sign-in" : "Sign-up"} </button>
                </div>

                <br/>
                {modeField}

            </form>
        </div>


    return (
        <div className="app">
            {element}
        </div>
    )
}

Login.propTypes = {
    setToken: PropTypes.func.isRequired
}