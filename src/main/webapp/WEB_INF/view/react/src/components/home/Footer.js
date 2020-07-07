import React from "react";
import {Col, Container, Navbar} from "react-bootstrap";

export default class Footer extends React.Component {
    render() {
        const footerStyle = {
            color:"white"
        };
        let fullYear = new Date().getFullYear();

        return (
            <Navbar fixed="bottom" bg="dark" variant="dark">
                <Container>
                    <Col lg={12} className="text-center text-muted">
                        <div style={footerStyle}>
                            {fullYear}-{fullYear+1}, All rights reserved by Meni Grossman
                        </div>
                    </Col>
                </Container>
            </Navbar>
        );
    }
}