import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Row } from 'react-bootstrap';
import { useFormik } from 'formik';
import { instance } from '../Axios';

const SendModal = (
  {
    showModal,
    handleClose,
    sendDataRows,
    setClientName,
    setDateFrom,
    setDateTo,
    setSuppliers,
    setFileMessage,
    formType,
    formKey
  }) => {

  useEffect(() => {
    instance.get('clients/all')
    .then(response => {
      setAddresses(response.data);
    })
    .catch(error => {
      console.error(error);
    });
  }, []);

  const [addresses, setAddresses] = useState([]);

  const validate = values => {
    const errors = {};
    if (!values.clientName) {
      errors.clientName = 'Укажите имя клиента';
    } else if (!addresses.includes(values.clientName)) {
      errors.clientName = 'Клиент отсутствует в списке. Проверьте корректность данных';
    }
    if (!values.dateFrom) {
      errors.dateFrom = 'Укажите дату действия';
    } else if (values.dateFrom > values.dateTo || new Date(values.dateFrom) < new Date('01.01.2020')) {
      errors.dateFrom = 'Дата начала действия не соотвествует требованиям или больше даты окончания';
    }
    if (!values.dateTo) {
      errors.dateTo = 'Укажите дату окончания действия';
    } else if (values.dateTo < values.dateFrom || new Date(values.dateTo) > new Date('01.01.2090')) {
      errors.dateTo = 'Дата окончания действия не соотвествует требованиям или меньше даты начала';
    }
    if (!values.comment) {
      errors.comment = 'Необходимо написать комментарий';
    }
    if (values.clientName === 'ПЛМ' && (!values.suppliers && (formType === 'ltl' || (formType === 'pooling' &&
      (formKey === 'pooling' || formKey === 'downtimePooling' || formKey === 'overweightPooling'))))) {
      errors.suppliers = 'Обязательно нужно указывать поставщика для клиента ПЛМ';
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      clientName: '',
      dateFrom: '',
      dateTo: '',
      comment: '',
      suppliers: ''
    },
    validate,
    onSubmit: () => {
      sendDataRows();
      formik.resetForm();
    },
  });

  return (
    <Modal show={showModal} onHide={handleClose} size="lg">
      <form onSubmit={formik.handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Дополнительные настройки</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Row className="mb-3">
            <Form.Group className="col">
              <Form.Label>Имя клиента</Form.Label>
              <input className="form-control"
                     onChange={(e) => {
                       setClientName(e.target.value);
                       formik.handleChange(e);
                     }}
                     value={formik.values.clientName}
                     list="addressesDataList"
                     id="clientName"
                     placeholder="Начните писать..."/>
              <datalist
                id="addressesDataList"
                defaultValue={''}>
                {addresses.map(element =>
                  <option
                    key={element}
                    value={element}>
                  </option>)
                }
              </datalist>
              {formik.errors.clientName ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{formik.errors.clientName}</p> : null}
            </Form.Group>
            {((formType === 'ltl' || (formType === 'pooling' &&
                  (formKey === 'pooling' || formKey === 'downtimePooling' || formKey === 'overweightPooling')))
                && formik.values.clientName === 'ПЛМ') &&
              <Form.Group className="col">
                <Form.Label>Поставщики</Form.Label>
                <Form.Control
                  type="text"
                  id="suppliers"
                  value={formik.values.suppliers}
                  onChange={(e) => {
                    setSuppliers(e.target.value);
                    formik.handleChange(e);
                  }}
                  aria-describedby="reasonText"
                />
                {formik.errors.suppliers ?
                  <p style={{
                    color: 'red',
                    marginTop: '5px',
                    marginBottom: '0'
                  }}>{formik.errors.suppliers}</p> : null}
              </Form.Group>}
          </Row>
          <Row>
            <Form.Group
              className="col">
              <Form.Label>Дата начала действия</Form.Label>
              <Form.Control
                type="date"
                value={formik.values.dateFrom}
                onChange={(e) => {
                  setDateFrom(e.target.value);
                  formik.handleChange(e);
                }}
                id="dateFrom"
                placeholder="Дата начала действия"/>
              {formik.errors.dateFrom ?
                <p style={{
                  color: 'red',
                  marginTop: '5px',
                  marginBottom: '0'
                }}>{formik.errors.dateFrom}</p> : null}
            </Form.Group>
            <Form.Group
              className="col">
              <Form.Label>Дата окончания дейстия</Form.Label>
              <Form.Control
                type="date"
                value={formik.values.dateTo}
                onChange={(e) => {
                  setDateTo(e.target.value);
                  formik.handleChange(e);
                }}
                id="dateTo"
                placeholder="Дата окончания дейстия"/>
            </Form.Group>
            {formik.errors.dateTo ?
              <p style={{
                color: 'red',
                marginTop: '5px',
                marginBottom: '0'
              }}>{formik.errors.dateTo}</p> : null}
          </Row>
          <Row className="mt-2">
            <Form.Group
              className="col">
              <Form.Label htmlFor="inputText5">Комментарий</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                type="text"
                id="comment"
                value={formik.values.comment}
                onChange={(e) => {
                  setFileMessage(e.target.value);
                  formik.handleChange(e);
                }}
                aria-describedby="reasonText"
              />
            </Form.Group>
            {formik.errors.comment ?
              <p style={{
                color: 'red',
                marginTop: '5px',
                marginBottom: '0'
              }}>{formik.errors.comment}</p> : null}
          </Row>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
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

export default SendModal;