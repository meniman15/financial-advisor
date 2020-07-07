import React from 'react';
import CardList from '../components/CardList';
import SearchBox from '../components/SearchBox';
import Cards from '../components/Cards';
import Scroll from '../components/Scroll';
import ErrorBoundry from '../components/ErrorBoundry';
import './App.css';

class App extends React.Component {
  constructor(){
    super();
    this.state = {
      cardList: Cards,
      searchfield:''
    }
  }
  
  onSearchChange = (event) => {
    this.setState({ searchfield : event.target.value }) 
    
  }

  render(){
    const filteredCards = this.state.cardList.filter(Cards =>{
      return Cards.firstName.toLowerCase().startsWith(this.state.searchfield.toLowerCase());
    });
    return (
      <div className='tc'>
        <h1> RoboFriends </h1>
        <SearchBox searchChange={this.onSearchChange}/>
        <Scroll>
          <ErrorBoundry >
            <CardList cards={filteredCards}/>
          </ErrorBoundry>
        </Scroll>
      </div>
    );  
  }
}

export default App;