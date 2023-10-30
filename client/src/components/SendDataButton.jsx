import React from 'react';
import { Button } from 'react-bootstrap';

const SendDataButton = ({sendHandler, clearAllHandler, clearFormHandler, eventKey}) => {
  return (
    <div className="d-flex justify-content-center fixed-bottom p-3">
      <Button onClick={sendHandler}>Отправить</Button>
      <Button onClick={() => clearFormHandler(eventKey)} style={{marginLeft: '5px'}}>Очистить данную форму</Button>
      <Button onClick={clearAllHandler} style={{marginLeft: '5px'}}>Очистить все формы</Button>
    </div>
  );
};

export default SendDataButton;