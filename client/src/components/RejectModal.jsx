import React, { useState } from 'react';
import { Button, Col, Form, Modal } from 'react-bootstrap';
import { useSelector } from 'react-redux';

const RejectModal = ({show, setShow, fileName, handleReject}) => {

  const [reason, setReason] = useState('');
  const [kind, setKind] = useState('');
  const { email } = useSelector(state => state.user);

  return (
    <Modal show={show} onHide={() => setShow(false)}>
      <Modal.Header closeButton>
        <Modal.Title>Выберите причину отклонения</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form.Select
          defaultValue={kind}
          aria-label="Reason"
          onChange={e => setKind(e.target.value)}
        >
          <option value={kind} disabled>Выберите причину</option>
          <option value="1">Ошибка пользователя</option>
          <option value="2">Ручная прогрузка</option>
          <option value="3">Административное отклонение</option>
        </Form.Select>
        {kind === '1' &&
          <Col className="mt-2">
            <Form.Label htmlFor="inputPassword5">Причина</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              value={reason}
              onChange={e => setReason(e.target.value)}
              type="text"
              id="reasonText"
              aria-describedby="reasonText"
            />
          </Col>
        }
      </Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={() => handleReject(email, fileName, reason, kind)}>
          Отправить
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default RejectModal;