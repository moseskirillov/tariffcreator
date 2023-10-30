import React from 'react';
import { Button, Form, Modal, Row } from 'react-bootstrap';
import { useFormik } from 'formik';

const PoolingModal = (
  {
    setCommodityCode,
    showModal,
    setShowModal,
    sendDataHandler
  }) => {

  const validate = values => {
    const errors = {};
    if (!values.commodityCode || values.commodityCode === 'Выберите код товара') {
      errors.commodityCode = 'Укажите код товара';
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      commodityCode: '',
    },
    validate,
    onSubmit: () => {
      setCommodityCode(formik.values.commodityCode);
      sendDataHandler();
      formik.resetForm();
    },
  });

  return (
    <Modal show={showModal} onHide={() => setShowModal(false)}>
      <form onSubmit={formik.handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Код товара</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Row className="mb-3">
            <Form.Group>
              <Form.Label>Код товара</Form.Label>
              <Form.Select
                id="commodityCode"
                defaultValue={formik.values.commodityCode}
                onChange={formik.handleChange}
              >
                <option>Выберите код товара</option>
                <option value="Alco">Alco</option>
                <option value="Food">Food</option>
                <option value="Non-food">Non-food</option>
              </Form.Select>
              {formik.errors.commodityCode ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{formik.errors.commodityCode}</p> : null}
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

export default PoolingModal;