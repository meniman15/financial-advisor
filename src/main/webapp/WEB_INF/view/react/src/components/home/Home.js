import React from "react";
import NavigationBar from "./NavigationBar";
import Footer from "./Footer";

export default class Home extends React.Component{
    render(){
        return(
            <div>
                <NavigationBar/>
                <Footer/>
            </div>
            )
    }
}