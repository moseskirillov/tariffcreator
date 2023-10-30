import React from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';

const MessageToast = ({show, setShow, message}) => {
  return (
    <ToastContainer position="top-end" className="me-3" style={{marginTop: '90px'}}>
      <Toast show={show} onClose={() => setShow(false)} delay={5000} autohide={true}>
        <Toast.Header>
          <strong className="me-auto">Tariff Tool</strong>
        </Toast.Header>
        <Toast.Body>{message}</Toast.Body>
      </Toast>
    </ToastContainer>
  );
};

export default MessageToast;