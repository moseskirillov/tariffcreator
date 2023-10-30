import React, { useState } from 'react';
import { Col, Modal, Nav, Row, Spinner, Tab } from 'react-bootstrap';
import LTLMain from '../../components/tables/ltl/LTLMain';
import LTLDowntime from '../../components/tables/ltl/LTLDowntime';
import LTLOverweight from '../../components/tables/ltl/LTLOverweight';
import SendModal from '../../components/SendModal';
import {
  ltlDowntimeColumns,
  ltlDowntimeNestedHeaders,
  ltlOverweightColHeaders,
  ltlPrrColHeaders,
  ltlRailwayNestedHeaders,
  ltlStandardNestedHeaders
} from '../../util/ltlTablesData';
import SendDataButton from '../../components/SendDataButton';
import LTLModal from '../../components/LTLModal';
import LtlRailway from '../../components/tables/ltl/LTLRailway';
import LTLPrr from '../../components/tables/ltl/LTLPrr';

const ltlMainColumnsNames = [
  'Склад заказчика',
  'Склад ФМ',
  'Доставка',
  'Тип грузовой единицы',
  'Город выгрузки',
  'Тип грузополучателя',
  'Тип кузова',
  'Стоимость доставки для 1 паллета',
  'Стоимость доставки для 2 паллет',
  'Стоимость доставки для 3 паллет',
  'Стоимость доставки для 4 паллет',
  'Стоимость доставки для 5 паллет',
  'Стоимость доставки для 6-8 паллет',
  'Стоимость доставки для 9-15 паллет',
  'Стоимость доставки для 16-20 паллет',
  'Стоимость доставки для 21-25 паллет'
];

const LtlPage = (
  {
    showModal,
    setShowModal,
    hotRefStandard,
    hotRefRailway,
    hotRefDowntime,
    hotRefOverweight,
    hotRefPrr,
    warehouses,
    searchNames,
    standardData,
    railwayData,
    downtimeData,
    overweightData,
    prrData,
    sendDataRows,
    clearAllHandler,
    setCommodityCode,
    setLtlDelivery,
    ltlReturn,
    setLtlReturn,
    ltlReturnType,
    setLtlReturnType,
    clearFormHandler,
    setClientName,
    setDateFrom,
    setDateTo,
    setSuppliers,
    ltlMainErrorsMessages,
    ltlRailwayErrorsMessages,
    ltlDowntimeErrorsMessages,
    ltlPrrErrorMessages,
    setShowErrorsModal,
    setFileMessage,
    ltlSurchargeOversize,
    setLtlSurchargeOversize
  }) => {

  const [key, setKey] = useState('standardLTL');
  const [commodityModal, setCommodityModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const sendHandler = () => {
    setIsLoading(true);
    validate();
    setTimeout(() => {
      if (ltlMainErrorsMessages.length > 0
        || ltlDowntimeErrorsMessages.length > 0
        || ltlRailwayErrorsMessages.length > 0
        || ltlPrrErrorMessages.length > 0) {
        setShowErrorsModal(true);
      } else {
        const poolingData = hotRefStandard.current.hotInstance.getData()[0][7];
        if (poolingData === '' || poolingData === null) {
          setShowModal(true);
        } else {
          setCommodityModal(true);
        }
      }
      setIsLoading(false);
    }, 1500);
  };

  const validate = () => {
    ltlMainErrorsMessages.length = 0;
    ltlRailwayErrorsMessages.length = 0;
    ltlDowntimeErrorsMessages.length = 0;
    ltlPrrErrorMessages.length = 0;
    const mainTableData = hotRefStandard.current?.hotInstance;
    const railTableData = hotRefRailway.current?.hotInstance;
    const downtimeTableData = hotRefDowntime.current?.hotInstance;
    const prrTableData = hotRefPrr.current?.hotInstance;
    validateLTLMain(mainTableData, ltlMainErrorsMessages, ltlMainColumnsNames, validateLTLAndRailwayMainRows);
    validateLTLMain(railTableData, ltlRailwayErrorsMessages, ltlMainColumnsNames, validateLTLAndRailwayMainRows);
    validateDowntime(downtimeTableData, ltlDowntimeErrorsMessages);
    validatePRR(prrTableData, ltlPrrErrorMessages);
  };

  const validateLTLMain = (data, ltlMainErrorsMessages, columnNames, validateRows) => {
    if (data.countRows() > 1 || data.countEmptyCols() < data.countCols()) {
      for (let i = 0; i < data.countRows(); i++) {
        data.validateRows([i], () => {
          validateRows(data, i);
          for (let j = 0; j < data.countCols(); j++) {
            if (data.getCellMeta(i, j).valid !== undefined && !data.getCellMeta(i, j).valid) {
              ltlMainErrorsMessages.push(`Не заполнено обязательное поле на строке ${i + 1} - ${columnNames[j]}`);
            }
          }
          data.render();
        });
      }
    }
  };

  const validateDowntime = (table, errorsMessages) => {
    table.validateRows([0, 1, 2, 3, 4, 5], () => {
      validateDowntimeRow(table, errorsMessages, 0, 1, 3);
      validateDowntimeRow(table, errorsMessages, 0, 1, 4);
      validateDowntimeRow(table, errorsMessages, 0, 1, 5);
      validateDowntimeRow(table, errorsMessages, 3, 4, 3);
      validateDowntimeRow(table, errorsMessages, 3, 4, 4);
      validateDowntimeRow(table, errorsMessages, 3, 4, 5);
      validateDowntimeRegionRow(table, errorsMessages, 2, 3);
      validateDowntimeRegionRow(table, errorsMessages, 2, 4);
      validateDowntimeRegionRow(table, errorsMessages, 2, 5);
      validateDowntimeRegionRow(table, errorsMessages, 5, 3);
      validateDowntimeRegionRow(table, errorsMessages, 5, 4);
      validateDowntimeRegionRow(table, errorsMessages, 5, 5);
      table.render();
    });
  };

  const validatePRR = (table, errorMessages) => {
    for (let i = 0; i < table.countRows(); i++) {
      validateRPPCell(table, i, 2, errorMessages);
    }
    table.render();
  };

  const validateRPPCell = (table, row, column, errorMessages) => {
    if (table.getCellMeta(row, column).valid !== undefined && !table.getCellMeta(row, column).valid) {
      errorMessages.push(`Не верно заполнено обязательное поле на строке ${row + 1} - ${table.getColHeader(column)}`);
    }
  };

  const validateLTLAndRailwayMainRows = (table, index) => {
    const cell1 = table.getDataAtCell(index, 0);
    const cell2 = table.getDataAtCell(index, 1);
    if (((cell1 !== null && cell1 !== '') && (cell2 !== null && cell2 !== ''))
      || ((cell1 === null || cell1 === '') && (cell2 == null || cell2 === ''))) {
      table.setCellMeta(index, 0, 'valid', false);
      table.setCellMeta(index, 1, 'valid', false);
    }
    for (let i = 2; i < 7; i++) {
      const cell = table.getDataAtCell(index, i);
      if (cell === null || cell === '') {
        table.setCellMeta(index, i, 'valid', false);
      }
    }
    const cell8 = table.getDataAtCell(index, 7);
    if (cell8 === null || cell8 === '' || cell8 === 0) {
      table.setCellMeta(index, 7, 'valid', false);
    }
    for (let i = 8; i < 16; i++) {
      const cell = table.getDataAtCell(index, i);
      if ((cell === null || cell === '') && (table.getDataAtCell(index, 3) === null || table.getDataAtCell(index, 3) !== 'LP')) {
        table.setCellMeta(index, i, 'valid', false);
      }
      if (i >= 8 && i <= 13) {
        if (table.getDataAtCell(index, i) === 0 && table.getDataAtCell(index, 3) !== 'LP') {
          table.setCellMeta(index, i, 'valid', false);
        }
      }
    }
  };

  const validateDowntimeRow = (table, errorsMessages, row12, row24, column) => {
    if ((table.getDataAtCell(row12, column) === null || table.getDataAtCell(row12, column) === '')
      && (table.getDataAtCell(row24, column) !== null && table.getDataAtCell(row24, column) !== '')
      || (table.getCellMeta(row12, column).valid !== undefined && !table.getCellMeta(row12, column).valid)) {
      errorsMessages.push(`Не заполнено обязательное поле на строке ${row12 + 1} - стоимость простоя ${table.getDataAtCell(row12, 1)} ${table.getColHeader(column)} для Москвы свыше 12 часов`);
      table.setCellMeta(row12, column, 'valid', false);
    } else if ((table.getDataAtCell(row12, column) !== null && table.getDataAtCell(row12, column) !== '')
      && (table.getDataAtCell(row24, column) === null || table.getDataAtCell(row24, column) === '')
      || (table.getCellMeta(row24, column).valid !== undefined && !table.getCellMeta(row24, column).valid)) {
      errorsMessages.push(`Не заполнено обязательное поле на строке ${row24 + 1} - стоимость простоя ${table.getDataAtCell(row24, 1)} ${table.getColHeader(column)} для Москвы свыше 24 часов`);
      table.setCellMeta(row24, column, 'valid', false);
    }
  };

  const validateDowntimeRegionRow = (table, errorsMessages, row, column) => {
    if (table.getCellMeta(row, column).valid !== undefined && !table.getCellMeta(row, column).valid) {
      errorsMessages.push(`Не заполнено обязательное поле на строке ${row + 1} - стоимость простоя ${table.getDataAtCell(row, 1)} ${table.getColHeader(column)} для регионов свыше 24 часов`);
      table.setCellMeta(row, column, 'valid', false);
    }
  };

  return (
    <>
      <Tab.Container id="ltl-tabs" activeKey={key} onSelect={(key) => setKey(key)}>
        <Row className="gx-0 mt-3 mb-3">
          <Col>
            <Nav variant="pills" className="d-flex justify-content-center">
              <Nav.Item>
                <Nav.Link eventKey="standardLTL">LTL доставка</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="railwayLTL">ЖД</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="downtimeLTL">Простой</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="overweightLTL">Перевес</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="prrLTL">ПРР</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <Row className="gx-0">
          <Col>
            <Tab.Content>
              <Tab.Pane mountOnEnter={true} eventKey="standardLTL">
                <LTLMain
                  hotRef={hotRefStandard}
                  data={standardData}
                  nestedHeaders={ltlStandardNestedHeaders}
                  warehouses={warehouses}
                  searchNames={searchNames}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="railwayLTL">
                <LtlRailway
                  hotRef={hotRefRailway}
                  data={railwayData}
                  nestedHeaders={ltlRailwayNestedHeaders}
                  warehouses={warehouses}
                  searchNames={searchNames}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="downtimeLTL">
                <LTLDowntime
                  hotRef={hotRefDowntime}
                  data={downtimeData}
                  nestedHeaders={ltlDowntimeNestedHeaders}
                  columns={ltlDowntimeColumns}
                  ltlDowntimeErrorsMessages={ltlDowntimeErrorsMessages}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="overweightLTL">
                <LTLOverweight
                  hotRef={hotRefOverweight}
                  data={overweightData}
                  colHeaders={ltlOverweightColHeaders}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="prrLTL">
                <LTLPrr
                  hotRef={hotRefPrr}
                  data={prrData}
                  colHeaders={ltlPrrColHeaders}
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
        sendDataRows={() => sendDataRows('ltl')}
        setClientName={setClientName}
        setDateFrom={setDateFrom}
        setDateTo={setDateTo}
        setSuppliers={setSuppliers}
        setFileMessage={setFileMessage}
        formType={'ltl'}
        formKey={key}
      />
      <LTLModal
        setCommodityCode={setCommodityCode}
        setLtlDelivery={setLtlDelivery}
        ltlReturn={ltlReturn}
        setLtlReturn={setLtlReturn}
        ltlReturnType={ltlReturnType}
        setLtlReturnType={setLtlReturnType}
        showModal={commodityModal}
        setShowModal={setCommodityModal}
        sendDataHandler={() => {
          setCommodityModal(false);
          setShowModal(true);
        }}
        ltlSurchargeOversize={ltlSurchargeOversize}
        setLtlSurchargeOversize={setLtlSurchargeOversize}
      />
      <Modal show={isLoading} fullscreen={true} backdrop={'static'} keyboard={false}>
        <Modal.Body className={'d-flex justify-content-center align-items-center'}>
          <Spinner animation="border" variant="dark"/>
        </Modal.Body>
      </Modal>
    </>
  );
};

export default LtlPage;