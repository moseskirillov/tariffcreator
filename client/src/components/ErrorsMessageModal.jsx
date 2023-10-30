import React from 'react';
import { Button, Modal } from 'react-bootstrap';

const ErrorsMessageModal = (
  {
    show,
    setShow,
    ltlMainErrorsMessages,
    ltlRailwayErrorsMessages,
    ltlDowntimeErrorsMessages,
    ltlPrrErrorsMessages,
    ftlIntercityErrorsMessages,
    ftlAdditionalErrorsMessages,
    ftlMoscowErrorsMessages,
    poolingErrorsMessages,
    poolingDowntimeErrorsMessages,
    poolingAdditionalErrorsMessages
  }) => {

  const handleClose = () => {
    setShow(false);
  }

  return (
    <Modal show={show} onHide={handleClose} scrollable={true} size={"lg"}>
      <Modal.Header closeButton>
        <Modal.Title>Ошибки заполнения</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {ltlMainErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>LTL стандарт:</h4> : null}
        <div>{ltlMainErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ltlRailwayErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>LTL ЖД:</h4> : null}
        <div>{ltlRailwayErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ltlDowntimeErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>LTL простой:</h4> : null}
        <div>{ltlDowntimeErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ltlPrrErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>LTL ПРР:</h4> : null}
        <div>{ltlPrrErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ftlIntercityErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>FTL межгород:</h4> : null}
        <div>{ftlIntercityErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ftlAdditionalErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>FTL доп. услуги:</h4> : null}
        <div>{ftlAdditionalErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {ftlMoscowErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>FTL Москва:</h4> : null}
        <div>{ftlMoscowErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {poolingErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>Pooling:</h4> : null}
        <div>{poolingErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {poolingDowntimeErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>Pooling:</h4> : null}
        <div>{poolingDowntimeErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
        {poolingAdditionalErrorsMessages.length > 0 ? <h4 style={{textAlign: 'center'}}>Pooling догруз:</h4> : null}
        <div>{poolingAdditionalErrorsMessages.map((message, index) => (
          <p style={{fontSize: '15px', marginBottom: '3px'}} key={index}>{index + 1}. {message}</p>
        ))}</div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          Закрыть
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ErrorsMessageModal;