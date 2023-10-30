import React, { useState } from 'react';
import { Col, Modal, Nav, Row, Spinner, Tab } from 'react-bootstrap';
import PoolingDowntime from '../../components/tables/pooling/PoolingDowntime';
import {
  poolingAdditionalColHeaders,
  poolingDowntimeColumns,
  poolingDowntimeNestedHeaders,
  poolingNestedHeaders,
  poolingOverweightColHeaders,
} from '../../util/poolingTablesData';
import PoolingOverweight from '../../components/tables/pooling/PoolingOverweight';
import SendDataButton from '../../components/SendDataButton';
import SendModal from '../../components/SendModal';
import PoolingMain from '../../components/tables/pooling/PoolingMain';
import PoolingModal from '../../components/PoolingModal';
import PoolingAdditional from '../../components/tables/pooling/PoolingAdditioanl';

const poolingMainColumnsNames = [
  'Склад заказчика',
  'Склад ФМ',
  'Доставка',
  'Тип грузовой единицы',
  'Город выгрузки',
  'Тип грузополучателя',
  'Тип кузова',
  'Стоимость доставки для 1-15 паллет',
  'Стоимость доставки для 16-20 паллет',
  'Стоимость доставки для 21-25 паллет',
  'Стоимость доставки для 26 паллет',
  'Стоимость доставки для 27 паллет',
  'Стоимость доставки для 28 паллет',
  'Стоимость доставки для 29 паллет',
  'Стоимость доставки для 30 паллет',
  'Стоимость доставки для 31 паллета',
  'Стоимость доставки для 32 паллеты',
  'Стоимость доставки для 33 паллеты'
];

const poolingAdditionalColumnsNames = [
  'Место загрузки',
  'Грузополучатель',
  'Тип груза',
  'Тоннажность',
  'Стоимость доставки FTL',
  'Скидка заказчику FTL',
  'Тариф за доставку FTL',
  'Тариф за забор'
];

const PoolingPage = (
  {
    showModal,
    setShowModal,
    hotRefPooling,
    downtimeDataRef,
    overweightDataRef,
    additionalDataRef,
    poolingData,
    warehouses,
    searchNames,
    poolingDowntimeData,
    poolingOverweightData,
    poolingAdditionalData,
    sendDataRows,
    clearAllHandler,
    setCommodityCode,
    clearFormHandler,
    setClientName,
    setDateFrom,
    setDateTo,
    setSuppliers,
    setFileMessage,
    setShowErrorsModal,
    poolingErrorsMessages,
    poolingDowntimeErrorsMessages,
    poolingAdditionalErrorsMessages
  }) => {

  const [key, setKey] = useState('pooling');
  const [commodityModal, setCommodityModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const sendHandler = () => {
    setIsLoading(true);
    validate();
    setTimeout(() => {
      if (poolingErrorsMessages.length > 0
        || poolingDowntimeErrorsMessages.length > 0
        || poolingAdditionalErrorsMessages.length > 0) {
        setShowErrorsModal(true);
      } else {
        const poolingData = hotRefPooling.current.hotInstance.getData()[0][6];
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
    poolingErrorsMessages.length = 0;
    poolingDowntimeErrorsMessages.length = 0;
    poolingAdditionalErrorsMessages.length = 0;
    const mainTableData = hotRefPooling.current?.hotInstance;
    const downtimeTableData = downtimeDataRef.current?.hotInstance;
    const additionalTableData = additionalDataRef.current?.hotInstance;
    validatePoolingMain(mainTableData, poolingErrorsMessages, poolingMainColumnsNames, validatePoolingMainRows);
    validatePoolingAdditional(additionalTableData, poolingAdditionalErrorsMessages, poolingAdditionalColumnsNames);
    validateDowntime(downtimeTableData, poolingDowntimeErrorsMessages);
  };

  const validatePoolingMain = (data, errorsMessages, columnNames, validateRows) => {
    if (data.countRows() > 1 || data.countEmptyCols() < data.countCols()) {
      for (let i = 0; i < data.countRows(); i++) {
        data.validateRows([i], () => {
          validateRows(data, i);
          for (let j = 0; j < data.countCols(); j++) {
            if (data.getCellMeta(i, j).valid !== undefined && !data.getCellMeta(i, j).valid) {
              errorsMessages.push(`Не заполнено обязательное поле на строке ${i + 1} - ${columnNames[j]}`);
            }
          }
          data.render();
        });
      }
    }
  };

  const validatePoolingMainRows = (table, index) => {
    const cell1 = table.getDataAtCell(index, 0);
    const cell2 = table.getDataAtCell(index, 1);
    if (((cell1 !== null && cell1 !== '') && (cell2 !== null && cell2 !== ''))
      || ((cell1 === null || cell1 === '') && (cell2 == null || cell2 === ''))) {
      table.setCellMeta(index, 0, 'valid', false);
      table.setCellMeta(index, 1, 'valid', false);
    }
    for (let i = 2; i < 17; i++) {
      const cell = table.getDataAtCell(index, i);
      if (cell === null || cell === '') {
        table.setCellMeta(index, i, 'valid', false);
      }
      if (i >= 6 && i <= 16) {
        if (table.getDataAtCell(index, i) === 0) {
          table.setCellMeta(index, i, 'valid', false);
        }
      }
    }
  };

  const validatePoolingAdditional = (data, errorsMessages, columnNames) => {
    if (data.countRows() > 1 || data.countEmptyCols() < data.countCols()) {
      for (let i = 0; i < data.countRows(); i++) {
        for (let j = 0; j < data.countCols(); j++) {
          const dataCell = data.getDataAtCell(i, j);
          if ((data.getCellMeta(i, j).valid !== undefined && !data.getCellMeta(i, j).valid) || (dataCell === null || dataCell === '')) {
            data.setCellMeta(i, j, 'valid', false);
            errorsMessages.push(`Не заполнено обязательное поле на строке ${i + 1} - ${columnNames[j]}`);
          } else {
            if (j === 4) {
              if (data.getDataAtCell(i, j) === 0) {
                data.setCellMeta(i, j, 'valid', false);
                errorsMessages.push(`Не верно заполнено обязательное поле на строке ${i + 1} - ${columnNames[j]}`);
              }
            } else {
              data.setCellMeta(i, j, 'valid', true);
            }
          }
        }
      }
      data.render();
    }
  };

  const validateDowntime = (table, errorsMessages) => {
    validateDowntimeRow(table, errorsMessages, 0, 1, 3);
    validateDowntimeRow(table, errorsMessages, 0, 1, 4);
    validateDowntimeRow(table, errorsMessages, 0, 1, 5);
    validateDowntimeRow(table, errorsMessages, 3, 4, 3);
    validateDowntimeRow(table, errorsMessages, 3, 4, 4);
    validateDowntimeRow(table, errorsMessages, 3, 4, 5);
    validateDowntimeRow(table, errorsMessages, 6, 7, 3);
    validateDowntimeRow(table, errorsMessages, 6, 7, 4);
    validateDowntimeRow(table, errorsMessages, 6, 7, 5);
    validateDowntimeRegionRow(table, errorsMessages, 2, 3);
    validateDowntimeRegionRow(table, errorsMessages, 2, 4);
    validateDowntimeRegionRow(table, errorsMessages, 2, 5);
    validateDowntimeRegionRow(table, errorsMessages, 5, 3);
    validateDowntimeRegionRow(table, errorsMessages, 5, 4);
    validateDowntimeRegionRow(table, errorsMessages, 5, 5);
    validateDowntimeRegionRow(table, errorsMessages, 8, 3);
    validateDowntimeRegionRow(table, errorsMessages, 8, 4);
    validateDowntimeRegionRow(table, errorsMessages, 8, 5);
    table.render();
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
                <Nav.Link eventKey="pooling">Pooling доставка</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="downtimePooling">Простой</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="overweightPooling">Перевес</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="additionalPooling">Догруз</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <Row className="gx-0">
          <Col>
            <Tab.Content>
              <Tab.Pane eventKey="pooling">
                <PoolingMain
                  hotRef={hotRefPooling}
                  data={poolingData}
                  nestedHeaders={poolingNestedHeaders}
                  warehouses={warehouses}
                  searchNames={searchNames}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="downtimePooling">
                <PoolingDowntime
                  hotRef={downtimeDataRef}
                  data={poolingDowntimeData}
                  nestedHeaders={poolingDowntimeNestedHeaders}
                  columns={poolingDowntimeColumns}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="overweightPooling">
                <PoolingOverweight
                  hotRef={overweightDataRef}
                  data={poolingOverweightData}
                  colHeaders={poolingOverweightColHeaders}
                />
              </Tab.Pane>
              <Tab.Pane eventKey="additionalPooling">
                <PoolingAdditional
                  hotRef={additionalDataRef}
                  data={poolingAdditionalData}
                  colHeaders={poolingAdditionalColHeaders}
                  warehouses={warehouses}
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
        sendDataRows={() => sendDataRows('pooling')}
        setClientName={setClientName}
        setDateFrom={setDateFrom}
        setDateTo={setDateTo}
        setSuppliers={setSuppliers}
        setFileMessage={setFileMessage}
        formType={'pooling'}
        formKey={key}
      />
      <PoolingModal
        setCommodityCode={setCommodityCode}
        showModal={commodityModal}
        setShowModal={setCommodityModal}
        sendDataHandler={() => {
          setCommodityModal(false);
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

export default PoolingPage;