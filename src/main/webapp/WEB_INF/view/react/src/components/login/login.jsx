import React from "react";
import loginImg from "./pic.svg"; /*./finAdvisorLoginPic.png*/
import axios from "axios";

export class Login extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username : '',
            password : '',
            error:""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }


    handleSubmit(event) {
        event.preventDefault();
        console.log("Login: "+this.state.username+ " "+ this.state.password);
        axios.post("Http://localhost:8080/login", {
            username: this.state.username,
            password: this.state.password
        }, {withCredentials: true})
            .then((response) => {
                this.props.history.push("/mainPage");
                this.props.handleSubmit();
            })
            .catch((error)=> {
                console.log("error: " + JSON.stringify(error));
                this.setState({error : (error.response.data.message)})
            });
    }

    handleChange(event){
        this.setState({[event.target.name] : event.target.value});
    }

    render() {
        return <div className="base-container" ref={this.props.containerRef}>
            <div className="header"> Login </div>
            <div className="content">
                <div className="image">
                    <img src={loginImg} alt="Try to remember your credentials..."/>

                </div>
                <form className="form" onSubmit={this.handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username"> Username</label>
                        <input type="text" name="username" placeholder="Username" value={this.state.username} required onChange={this.handleChange}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="password"> Password</label>
                        <input type="password" name="password" placeholder="Password" value={this.state.password} required onChange={this.handleChange}/>
                    </div>
                    {this.state.error.length > 0 &&
                    <span className='error'>{this.state.error}</span>}
                    <div className="footer">
                        <button type="submit" className="btn left-btn">
                            Login
                        </button>
                        <button type="button" className="btn right-btn" onClick={() => this.props.redirectMethod("register")}>
                            Register
                        </button>
                    </div>
                </form>
            </div>

        </div>
    }
}