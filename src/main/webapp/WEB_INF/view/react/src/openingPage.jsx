import React from 'react';
import './App.scss';
import {Login, Register} from "./components/login/index";

class OpeningPage extends React.Component {

    constructor(props){
        super(props);
        this.state = {
            isLogin : true,
            isRegister : false
        }
    }

    componentDidMount() {
        //Add .right by default
        this.rightSide.classList.add("right");
    }

    changeState = () => {
        const { isLogin} = this.state;
        if (isLogin) {
            this.rightSide.classList.remove("right");
            this.rightSide.classList.add("left");
        }
        else{
            this.rightSide.classList.remove("left");
            this.rightSide.classList.add("right");
        }
        this.setState(prevState => ({ isLogin : !prevState.isLogin}));
    };

    submitAndMoveToApp = () => {
        this.props.onSubmitDetails();
    };

    render(){
        const current = this.state.isLogin ? "Register" : "Login";
        const {isLogin} = this.state;
        return (
            <div className="App">
                <div className="login">
                    <div className="container" ref={ref => (this.container = ref)}>
                        {isLogin && (
                            <Login containerRef={(ref)=> this.current = ref}
                                    handleSubmit = { this.submitAndMoveToApp } redirectMethod = {() =>{this.changeState(); this.setState({isLogin: false , isRegister : true})}} {...this.props}/>
                        )}
                        {!isLogin && (
                            <Register containerRef={(ref)=> this.current = ref}
                                      handleSubmit = { this.submitAndMoveToApp } redirectMethod = { () => {this.changeState(); this.setState({isLogin: true , isRegister : false}); }} {...this.props}/>
                        )}
                    </div>
                    <RightSide
                        current={current}
                        containerRef={ref => this.rightSide = ref}
                        onClick={this.changeState.bind(this)}
                    />
                </div>
            </div>
        );
    }
}

const RightSide = props => {
    return (
        <div className="right-side"
             ref={props.containerRef}
             onClick={props.onClick}>
            <div className="inner-container">
                <div className="text">{props.current}</div>
            </div>
        </div>
    );
}
export default OpeningPage;
