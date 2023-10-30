import React from 'react';
import { Col, Form, Row, Spinner } from 'react-bootstrap';

const SearchPanel = ({download, search}) => {
  return (
    <>
      <Row className="gx-0 justify-content-center mb-3">
        <Col lg={4}>
          <Form.Control
            onChange={(e) => search(e.target.value)}
            type="search"
            placeholder="Поиск"
            className="me-2"
            aria-label="Search"
          />
        </Col>
      </Row>
      <Row className="justify-content-center">
        {download &&
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
          </Spinner>}
      </Row>
    </>
  );
};

export default SearchPanel;