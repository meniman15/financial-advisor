import React from 'react';
import './App.scss';
import Home from './components/home/Home.js';
import OpeningPage from "./openingPage";
import axios from "axios";
import {Route, Router, Switch} from "react-router-dom";
import {createBrowserHistory} from 'history';
import Backtester from "./components/home/Backtester";
import NavigationBar from "./components/home/NavigationBar";
import Graph from "./components/home/Graph";
import CalculatorPage from "./components/home/CalculatorPage";
/*import 'typeface-roboto';*/

const history = createBrowserHistory();

class App extends React.Component {

  constructor(props){
    super(props);
    this.state = {
        isFirstPage : true
    }
  }

  componentDidMount() {
    if (window.location.pathname === "/login"){
      axios.get("http://localhost:8080/login",{withCredentials : true}).then(response => {
          console.log(JSON.stringify(response));
          console.log(response.data);
          if (response.data === true){
              history.push("/mainPage");
          }
      });
    }
  }

  onOpeningPageSubmit = () => {
      console.log("changed isFirstPage to false");
      this.setState({isFirstPage : false});
  };

  render(){
     return (
         <div className="App">
             <Router history={history}>
                 <Switch>
                     <Route exact path={"/login"}
                     render={props => (
                         <OpeningPage
                         ref={ref=> (this.current = ref)}
                         onSubmitDetails = {this.onOpeningPageSubmit} {...props}/>
                         )}/>
                     <Route exact path="/mainPage"
                        render={props => (
                            <Home/>
                        )}/>
                     <Route exact path="/backtester" render={props => (
                         <div>
                             <NavigationBar/>
                             <Backtester/>
                         </div>
                     )}/>
                     <Route exact path="/graph" render={props => (
                         <div>
                             <NavigationBar/>
                             <Graph/>
                         </div>
                     )}/>
                     <Route exact path="/budget" render={props => (
                         <div>
                             <NavigationBar/>
                         </div>
                     )}/>
                     <Route exact path="/calculator" render={props => (
                         <div>
                             <NavigationBar/>
                             <CalculatorPage/>
                         </div>
                     )}/>
                 </Switch>
             </Router>
        </div>
     );
  }
}

export default App;