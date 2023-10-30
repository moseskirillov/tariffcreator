import React, { useState } from 'react';
import { Col, Modal, Nav, Row, Spinner, Tab } from 'react-bootstrap';
import SendDataButton from '../../components/SendDataButton';
import SendModal from '../../components/SendModal';
import FTLAdditional from '../../components/tables/ftl/FTLAdditional';
import { ftlAdditionalColHeaders, ftlIntercityColHeaders, ftlMoscowNestedHeaders } from '../../util/ftlTablesData';
import FTLIntercity from '../../components/tables/ftl/FTLIntercity';
import FTLMoscow from '../../components/tables/ftl/FTLMoscow';
import FTLModal from '../../components/FTLModal';

const ftlMscColumnsName = [
  'Место загрузки',
  'Тип ТС',
  'Вместимость паллет',
  'Тип кузова',
  'Мин. Оплачиваемое Время',
  '1 зона',
  '2 зона',
  '3 зона',
  '4 зона',
  'простой',
  'доп. точка',
  'ПРР',
  'экспедирование'
];

const ftlIntercityColumnsNames = [
  'Пункт отправления',
  'Пункт назначения',
  'Тип ТС',
  'Тент',
  'Изо',
  'Реф',
];

const FtlPage = (
  {
    showModal,
    setShowModal,
    hotRefIntercity,
    hotRefAdditional,
    hotRefMoscow,
    intercityData,
    additionalData,
    moscowData,
    warehouses,
    billing,
    setBilling,
    sendDataRows,
    clearAllHandler,
    clearFormHandler,
    setClientName,
    setDateFrom,
    setDateTo,
    setFileMessage,
    setShowErrorsModal,
    ftlIntercityErrorsMessages,
    ftlAdditionalErrorsMessages,
    ftlMoscowErrorsMessages
  }
) => {

  const [billingModal, setBillingModal] = useState(false);
  const [key, setKey] = useState('intercityFTL');
  const [isLoading, setIsLoading] = useState(false);

  const sendHandler = () => {
    setIsLoading(true);
    validate();
    setTimeout(() => {
      if (ftlMoscowErrorsMessages.length === 0 && ftlIntercityErrorsMessages.length === 0 && ftlAdditionalErrorsMessages.length === 0) {
        const ftlData = hotRefMoscow?.current.hotInstance.getData()[0][0];
        if (ftlData === '' || ftlData === null) {
          setShowModal(true);
        } else {
          setBillingModal(true);
        }
      } else {
        setShowErrorsModal(true);
      }
      setIsLoading(false);
    }, 1500);
  };

  const validate = () => {
    ftlMoscowErrorsMessages.length = 0;
    ftlAdditionalErrorsMessages.length = 0;
    ftlIntercityErrorsMessages.length = 0;
    const mscTableData = hotRefMoscow.current?.hotInstance;
    const additionalTableData = hotRefAdditional.current?.hotInstance;
    const intercityTableData = hotRefIntercity.current?.hotInstance;
    validateTables(mscTableData, ftlMoscowErrorsMessages, ftlMscColumnsName, validateFTLMscRows);
    validateAdditionalRows(additionalTableData, ftlAdditionalErrorsMessages);
    validateTables(intercityTableData, ftlIntercityErrorsMessages, ftlIntercityColumnsNames, validateFTLIntercityRows);
  };

  const validateAdditionalRows = (table, errorsMessages) => {
    for (let i = 0; i < table.countRows(); i++) {
      if (table.getCellMeta(i, 4).valid !== undefined && !table.getCellMeta(i, 4).valid) {
        errorsMessages.push(`Не верно заполнено поле на строке ${i + 1} - ${table.getColHeader(4)}`);
      }
      if (table.getCellMeta(i, 5).valid !== undefined && !table.getCellMeta(i, 5).valid) {
        errorsMessages.push(`Не верно заполнено поле на строке ${i + 1} - ${table.getColHeader(5)}`);
      }
      if (table.getCellMeta(i, 6).valid !== undefined && !table.getCellMeta(i, 6).valid) {
        errorsMessages.push(`Не верно заполнено поле на строке ${i + 1} - ${table.getColHeader(6)}`);
      }
    }
    table.render();
  };

  const validateTables = (tableData, errorsArray, columnNames, validateRows) => {
    if (tableData.countRows() > 1 || tableData.countEmptyCols() < tableData.countCols()) {
      for (let i = 0; i < tableData.countRows(); i++) {
        tableData.validateRows([i], () => {
          validateRows(tableData, i);
          for (let j = 0; j < tableData.countCols(); j++) {
            if (tableData.getCellMeta(i, j).valid !== undefined && !tableData.getCellMeta(i, j).valid) {
              errorsArray.push(`Не заполнено обязательное поле на строке ${i + 1} - ${columnNames[j]}`);
            }
          }
          tableData.render();
        });
      }
    }
  };

  const validateFTLMscRows = (table, index) => {
    const firstZoneCell = table.getDataAtCell(index, 5);
    const secondZoneCell = table.getDataAtCell(index, 6);
    const thirdZoneCell = table.getDataAtCell(index, 7);
    const fourthZoneCell = table.getDataAtCell(index, 8);
    const downtimeCell = table.getDataAtCell(index, 9);
    const addCell = table.getDataAtCell(index, 10);
    const prrCell = table.getDataAtCell(index, 11);
    const expCell = table.getDataAtCell(index, 12);
    if ((downtimeCell !== null && downtimeCell !== '')
      && ((firstZoneCell === null || firstZoneCell === '')
        && (secondZoneCell === null || secondZoneCell === '')
        && (thirdZoneCell === null || thirdZoneCell === '')
        && (fourthZoneCell === null || fourthZoneCell === ''))) {
      table.setCellMeta(index, 5, 'valid', false);
      table.setCellMeta(index, 6, 'valid', false);
      table.setCellMeta(index, 7, 'valid', false);
      table.setCellMeta(index, 8, 'valid', false);
    } else if (((downtimeCell === null || downtimeCell === '')
        && (addCell === null || addCell === '')
        && (prrCell === null || prrCell === '')
        && (expCell === null || expCell === ''))
      && ((firstZoneCell === null || firstZoneCell === '')
        && (secondZoneCell === null || secondZoneCell === '')
        && (thirdZoneCell === null || thirdZoneCell === '')
        && (fourthZoneCell === null || fourthZoneCell === ''))) {
      table.setCellMeta(index, 5, 'valid', false);
      table.setCellMeta(index, 6, 'valid', false);
      table.setCellMeta(index, 7, 'valid', false);
      table.setCellMeta(index, 8, 'valid', false);
      table.setCellMeta(index, 9, 'valid', false);
      table.setCellMeta(index, 10, 'valid', false);
      table.setCellMeta(index, 11, 'valid', false);
      table.setCellMeta(index, 12, 'valid', false);
    }
  };

  const validateFTLIntercityRows = (table, index) => {
    for (let i = 0; i < 3; i++) {
      const cell = table.getDataAtCell(index, i);
      if (cell === null || cell === '') {
        table.setCellMeta(index, i, 'valid', false);
      }
    }
    const tent = table.getDataAtCell(index, 3);
    const izo = table.getDataAtCell(index, 4);
    const ref = table.getDataAtCell(index, 5);
    if ((tent === null || tent === '') && (ref === null || ref === '') && (izo === null || izo === '')) {
      table.setCellMeta(index, 3, 'valid', false);
      table.setCellMeta(index, 4, 'valid', false);
      table.setCellMeta(index, 5, 'valid', false);
    }
  };

  return (
    <>
      <Tab.Container id="ltl-tabs" activeKey={key} onSelect={(key) => setKey(key)}>
        <Row className="gx-0 mt-3 mb-3">
          <Col>
            <Nav variant="pills" className="d-flex justify-content-center">
              <Nav.Item>
                <Nav.Link eventKey="intercityFTL">Межгород доставка</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="additionalFTL">Доп. услуги межгород</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="moscowFTL">Москва доставка и доп. услуги</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <Row className="gx-0">
          <Col>
            <Tab.Content>
              <Tab.Pane eventKey="intercityFTL">
                <FTLIntercity
                  hotRef={hotRefIntercity}
                  data={intercityData}
                  colHeaders={ftlIntercityColHeaders}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="additionalFTL">
                <FTLAdditional
                  hotRef={hotRefAdditional}
                  data={additionalData}
                  colHeaders={ftlAdditionalColHeaders}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="moscowFTL">
                <FTLMoscow
                  hotRef={hotRefMoscow}
                  data={moscowData}
                  warehouses={warehouses}
                  nestedHeaders={ftlMoscowNestedHeaders}
                />
              </Tab.Pane>
            </Tab.Content>
          </Col>
        </Row>
      </Tab.Container>
      <SendDataButton
        sendHandler={sendHandler}
        clearAllHandler={clearAllHandler}
        clearFormHandler={clearFormHandler}
        eventKey={key}
      />
      <SendModal
        showModal={showModal}
        handleClose={() => setShowModal(false)}
        sendDataRows={() => sendDataRows('ftl')}
        setClientName={setClientName}
        setDateFrom={setDateFrom}
        setDateTo={setDateTo}
        setFileMessage={setFileMessage}
      />
      <FTLModal
        billing={billing}
        setBilling={setBilling}
        showModal={billingModal}
        setShowModal={setBillingModal}
        sendDataHandler={() => {
          setBillingModal(false);
          setShowModal(true);
        }}
      />
      <Modal show={isLoading} fullscreen={true} backdrop={'static'} keyboard={false}>
        <Modal.Body className={'d-flex justify-content-center align-items-center'}>
          <Spinner animation="border" variant="dark"/>
        </Modal.Body>
      </Modal>
    </>
  );
};

export default FtlPage;