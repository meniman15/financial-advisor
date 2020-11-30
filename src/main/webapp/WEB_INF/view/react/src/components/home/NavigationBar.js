import React from "react";
import {Nav, Navbar} from "react-bootstrap";

class NavigationBar extends React.Component {
    render() {
        return (
            <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
                <Navbar.Brand href="/mainPage">#Financial Advisor</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="mr-auto">
                        <Nav.Link href="/calculator">Stock Calculator</Nav.Link>
                        <Nav.Link href="/budget">Budget</Nav.Link>
                        <Nav.Link href="/graph">Graph</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default NavigationBar;