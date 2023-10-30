import React from 'react';
import { Button, Form, Modal, Row } from 'react-bootstrap';
import { useFormik } from 'formik';

const LtlModal = (
  {
    showModal,
    setShowModal,
    setLtlDelivery,
    ltlReturn,
    ltlReturnType,
    setLtlReturnType,
    setLtlReturn,
    setCommodityCode,
    sendDataHandler,
    ltlSurchargeOversize,
    setLtlSurchargeOversize
  }) => {

  const validate = values => {
    console.log(values.commodityCode);
    console.log(values.isDelivery);
    console.log(values.isReturn);
    const errors = {};
    if (!values.commodityCode || values.commodityCode === 'Выберите код товара') {
      errors.commodityCode = 'Укажите код товара';
    }
    if (!values.isDelivery && !values.isReturn) {
      errors.isDelivery = 'Необходимо выбрать опцию доставки или возврата';
      errors.isReturn = 'Необходимо выбрать опцию доставки или возврата';
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      commodityCode: '',
      isDelivery: false,
      isReturn: false
    },
    validate,
    onSubmit: () => {
      setCommodityCode(formik.values.commodityCode);
      setLtlDelivery(formik.values.isDelivery);
      setLtlReturn(formik.values.isReturn);
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
          <Row>
            <Form.Group className="mb-3" controlId="deliveryCheck">
              <Form.Check
                id="isDelivery"
                name="isDelivery"
                type="checkbox"
                label="Доставка"
                value="isDelivery"
                onChange={(e) => {
                  formik.handleChange(e);
                  setLtlDelivery(formik.values.isDelivery);
                }}/>
              {formik.errors.isDelivery ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{!formik.errors.isDelivery}</p> : null}
            </Form.Group>
            <Form.Group className="mb-3" controlId="returnCheck">
              <Form.Check
                id="isReturn"
                name="isReturn"
                type="checkbox"
                label="Возврат"
                value="isReturn"
                onChange={(e) => {
                  formik.handleChange(e);
                  setLtlReturn(!formik.values.isReturn);
                }}/>
              {formik.errors.isReturn ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{formik.errors.isReturn}</p> : null}
            </Form.Group>
            {!ltlReturn &&
              <Form.Group className="mb-3" controlId="oversizeCheck">
                <Form.Check
                  type="checkbox"
                  label="Надбавка 50% для негабарита (OT)"
                  checked={ltlSurchargeOversize}
                  onChange={() => setLtlSurchargeOversize(!ltlSurchargeOversize)}
                />
              </Form.Group>
            }
          </Row>
          {ltlReturn &&
            <Row className="mb-3">
              <Form.Group>
                <Form.Label>Тип возврата</Form.Label>
                <Form.Select
                  defaultValue={ltlReturnType}
                  onChange={e => setLtlReturnType(e.target.value)} required
                >
                  <option value={null}>Выберите тип</option>
                  <option value="Standard">Без наценки: 100% от тарифа доставки</option>
                  <option value="Margin">С наценкой: + 150% к тарифу доставки</option>
                </Form.Select>
              </Form.Group>
            </Row>
          }
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

export default LtlModal;