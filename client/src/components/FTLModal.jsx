import React from 'react';
import { Button, Form, Modal, Row } from 'react-bootstrap';
import { useFormik } from 'formik';

const FTLModal = (
  {
    billing,
    setBilling,
    showModal,
    setShowModal,
    sendDataHandler
  }) => {

  const validate = values => {
    const errors = {};
    if (!values.billing || values.billing === 'Тарификация') {
      errors.billing = 'Необходимо выбрать опцию доставки или возврата';
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      billing: '',
    },
    validate,
    onSubmit: () => {
      setBilling(formik.values.billing);
      formik.resetForm();
      sendDataHandler();
    },
  });

  return (
    <Modal show={showModal} onHide={() => setShowModal(false)}>
      <form onSubmit={formik.handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Опции</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Row className="mb-3">
            <Form.Group>
              <Form.Select
                id="billing"
                defaultValue={formik.values.billing}
                onChange={formik.handleChange}
              >
                <option>Тарификация</option>
                <option value="TC">По типу ТС</option>
                <option value="capacity">По вместимости</option>
              </Form.Select>
              {formik.errors.billing ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{formik.errors.billing}</p> : null}
            </Form.Group>
          </Row>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Закрыть
          </Button>
          <Button variant="primary" type={'submit'}>
            Отправить
          </Button>
        </Modal.Footer>
      </form>
    </Modal>
  );
};

export default FTLModal;