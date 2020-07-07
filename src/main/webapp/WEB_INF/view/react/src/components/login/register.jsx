import React from "react";
import RegisterImg from "./finRegisterPic.png";
import axios from "axios";

export class Register extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username : '',
            password : '',
            email : '',
            errors: {
                username: '',
                email: '',
                password: '',
            }
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    validUsernameAndPasswordRegex = RegExp( /^[0-9a-zA-Z]+$/);
    validEmailRegex = RegExp(/^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i);
    validateForm = (errors) => {
        let valid = true;
        Object.values(errors).forEach(
            (val) => val.length > 0 && (valid = false)
        );
        return valid;
    }

    handleSubmit(event) {
        event.preventDefault();
        if(this.validateForm(this.state.errors)) {
            console.log("Register: "+this.state.username+ " "+ this.state.password + " " + this.state.email);
            axios.post("http://localhost:8080/register", {
                    username: this.state.username,
                    password: this.state.password,
                    email: this.state.email
                }, { withCredentials : true}
            ).then(response => {
                console.log("response: "+ JSON.stringify(response));
                this.props.handleSubmit();
                this.props.history.push("/mainPage");
            }).catch(error => {
                console.log("error: " + JSON.stringify(error));
            })
        }else{
            console.error('Invalid Form')
        }
    }

    handleChange(event){
        event.preventDefault();
        const { name, value } = event.target;
        let errors = this.state.errors;
        switch (name) {
            case 'username':
                errors.username =
                    !this.validUsernameAndPasswordRegex.test(value) && value.length < 5
                        ? 'Min. 5 characters, English characters/numbers only.'
                        : !this.validUsernameAndPasswordRegex.test(value) ? 'Only English characters and numbers are permitted.'
                        : value.length < 5 ? 'Username must be 5 characters long!' : '';
                break;
            case 'email':
                errors.email =
                    this.validEmailRegex.test(value)
                        ? ''
                        : 'Email is not valid!';
                break;
            case 'password':
                errors.password =
                    !this.validUsernameAndPasswordRegex.test(value) && value.length < 8
                        ? 'Min. 8 characters,English characters/numbers only.'
                        : !this.validUsernameAndPasswordRegex.test(value) ? 'Only English characters and numbers are permitted.'
                        : value.length < 8 ? 'Password must be 8 characters long!' : '';
                break;
            default:
                break;
        }

        this.setState({[name] : value , errors : errors})

    }

    render() {
        return <div className="base-container" ref={this.props.containerRef}>
            <div className="header"> Register </div>
            <div className="content">
                <div className="image">
                    <img className="register-img" src={RegisterImg} alt="What are you doing here? the important stuff is down below!"/>

                </div>
                <form className="form" onSubmit={this.handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username"> Username</label>
                        <input type="text" name="username" placeholder="Username" required value={this.state.username} onChange={this.handleChange}/>
                        {this.state.errors.username.length > 0 &&
                        <span className='error'>{this.state.errors.username}</span>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="password"> Password</label>
                        <input type="password" name="password" placeholder="Password" required value={this.state.password} onChange={this.handleChange}/>
                        {this.state.errors.password.length > 0 &&
                        <span className='error'>{this.state.errors.password}</span>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="email"> Email</label>
                        <input type="email" name="email" placeholder="Email" required value={this.state.email} onChange={this.handleChange}/>
                        {this.state.errors.email.length > 0 &&
                        <span className='error'>{this.state.errors.email}</span>}
                    </div>
                    <div className="footer">
                        <button type="submit" className="btn left-btn">
                            Register
                        </button>
                        <button type="button" className="btn right-btn" onClick={() => this.props.redirectMethod("login")}>
                            Back
                        </button>
                    </div>
                </form>

            </div>

        </div>
    }
}