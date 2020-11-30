import React from "react";
import NavigationBar from "./NavigationBar";
import Footer from "./Footer";

export default class Home extends React.Component{
    render(){
        return(
            <div>
                <NavigationBar/>
                <h1> ברוכים הבאים לבקטסט ישראל</h1>
                <Footer/>
            </div>
            )
    }
}