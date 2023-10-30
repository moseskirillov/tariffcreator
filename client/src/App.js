import './App.css';
import Layout from './layout/Layout';
import { Navigate, Route, Routes } from 'react-router-dom';
import NotFound from './pages/NotFound';
import Login from './pages/Login';
import LTLPage from './pages/tables/LTLPage';
import 'handsontable/dist/handsontable.full.min.css';
import { registerAllCellTypes, registerAllModules } from 'handsontable/registry';
import PoolingPage from './pages/tables/PoolingPage';
import Validation from './pages/Validation';
import FTLPage from './pages/tables/FTLPage';
import { useDispatch, useSelector } from 'react-redux';
import { useEffect, useRef, useState } from 'react';
import {
  ltlDowntimeDataUtil,
  ltlOverweightDataUtil,
  ltlPrrDataUtil,
  ltlRailwayDataUtil,
  ltlStandardDataUtil
} from './util/ltlTablesData';
import {
  poolingAdditionalDataUtil,
  poolingDataUtil,
  poolingDowntimeDataUtil,
  poolingOverweightDataUtil
} from './util/poolingTablesData';
import { ftlAdditionalDataUtil, ftlIntercityDataUtil, ftlMoscowDataUtil } from './util/ftlTablesData';
import { generateFTL, generateLTL, generatePooling } from './services/generator.service';
import { instance } from './Axios';
import { searchNames } from './util/tariffsPooling';
import MessageToast from './components/MessageToast';
import PrivateRoute from './components/PrivateRoute';
import AuthRoute from './components/AuthRoute';
import Logout from './components/Logout';
import { login, logout } from './store/slices/user';
import Register from './pages/Register';
import ResetPassword from './pages/ResetPassword';
import ChangePassword from './pages/ChangePassword';
import ErrorsMessageModal from './components/ErrorsMessageModal';
import { registerLanguageDictionary, ruRU } from 'handsontable/i18n';

function App() {

  registerAllModules();
  registerAllCellTypes();
  registerLanguageDictionary(ruRU);

  const dispatch = useDispatch();
  const { email, refreshToken } = useSelector(state => state.user);

  const [showModal, setShowModal] = useState(false);

  const [clientName, setClientName] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [suppliers, setSuppliers] = useState('');

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('');

  const ltlStandardDataRef = useRef(null);
  const ltlRailwayDataRef = useRef(null);
  const ltlDowntimeDataRef = useRef(null);
  const ltlOverweightDataRef = useRef(null);
  const ltlPrrDataRef = useRef(null);

  const ftlIntercityDataRef = useRef(null);
  const ftlAdditionalDataRef = useRef(null);
  const ftlMoscowDataRef = useRef(null);

  const poolingDataRef = useRef(null);
  const poolingDowntimeDataRef = useRef(null);
  const poolingOverweightDataRef = useRef(null);
  const poolingAdditionalDataRef = useRef(null);

  const [ltlStandardData, setLtlStandardData] = useState(ltlStandardDataUtil);
  const [ltlRailwayData, setLtlRailwayData] = useState(ltlRailwayDataUtil);
  const [ltlDowntimeData, setLtlDowntimeData] = useState(ltlDowntimeDataUtil);
  const [ltlOverweightData, setLtlOverweightData] = useState(ltlOverweightDataUtil);
  const [ltlPrrData, setLtlPrrData] = useState(ltlPrrDataUtil);

  const [poolingData, setPoolingData] = useState(poolingDataUtil);
  const [poolingDowntimeData, setPoolingDowntimeData] = useState(poolingDowntimeDataUtil);
  const [poolingOverweightData, setPoolingOverweightData] = useState(poolingOverweightDataUtil);
  const [poolingAdditionalData, setPoolingAdditionalData] = useState(poolingAdditionalDataUtil);

  const [ftlIntercityData, setFtlIntercityData] = useState(ftlIntercityDataUtil);
  const [ftlAdditionalData, setFtlAdditionalData] = useState(ftlAdditionalDataUtil);
  const [ftlMoscowData, setFtlMoscowData] = useState(ftlMoscowDataUtil);

  const [warehouses, setWarehouses] = useState([]);
  const [commodityCode, setCommodityCode] = useState('');
  const [billing, setBilling] = useState('');

  const [ltlDelivery, setLtlDelivery] = useState(false);
  const [ltlReturn, setLtlReturn] = useState(false);
  const [ltlSurchargeOversize, setLtlSurchargeOversize] = useState(false);
  const [ltlReturnType, setLtlReturnType] = useState(null);

  const [ltlMainErrorsMessages, setLtlMainErrorsMessages] = useState([]);
  const [ltlRailwayErrorsMessages, setLtlRailwayErrorsMessages] = useState([]);
  const [ltlDowntimeErrorsMessages, setLtlDowntimeErrorsMessages] = useState([]);
  const [ltlPrrErrorsMessages, setLtlPrrErrorsMessages] = useState([]);

  const [ftlIntercityErrorsMessages, setFtlIntercityErrorsMessages] = useState([]);
  const [ftlAdditionalErrorsMessages, setFtlAdditionalErrorsMessages] = useState([]);
  const [ftlMoscowErrorsMessages, setFtlMoscowErrorsMessages] = useState([]);

  const [poolingErrorsMessages, setPoolingErrorsMessages] = useState([]);
  const [poolingDowntimeErrorsMessages, setPoolingDowntimeErrorsMessages] = useState([]);
  const [poolingAdditionalErrorsMessages, setPoolingAdditionalErrorsMessages] = useState([]);

  const [showErrorsModal, setShowErrorsModal] = useState(false);

  const [fileMessage, setFileMessage] = useState('');

  useEffect(() => {
    checkAuth();
    instance.get('search/all')
    .then(response => {
      setWarehouses(response.data);
    })
    .catch(error => {
      console.error(error);
    });
  }, []);

  const checkAuth = () => {
    if (localStorage.getItem('user-refreshToken') !== ''
      && localStorage.getItem('user-refreshToken') !== null) {
      instance.post('/auth/refresh', {
        refreshToken: refreshToken
      }).then(response => {
        dispatch(login({
          email: response.data.email,
          roles: response.data.roles,
          accessToken: response.data.accessToken,
          refreshToken: response.data.refreshToken
        }));
      }).catch(error => {
        console.error(error);
        dispatch(logout);
      });
    } else {
      dispatch(logout);
    }
  };

  const clearAllHandler = () => {
    setLtlStandardData(ltlStandardDataUtil);
    setLtlRailwayData(ltlRailwayDataUtil);
    setLtlDowntimeData(ltlDowntimeDataUtil);
    setLtlOverweightData(ltlOverweightDataUtil);
    setLtlPrrData(ltlPrrDataUtil);
    setPoolingData(poolingDataUtil);
    setPoolingDowntimeData(poolingDowntimeDataUtil);
    setPoolingOverweightData(poolingOverweightDataUtil);
    setPoolingAdditionalData(poolingAdditionalDataUtil);
    setFtlIntercityData(ftlIntercityDataUtil);
    setFtlAdditionalData(ftlAdditionalDataUtil);
    setFtlMoscowData(ftlMoscowDataUtil);
    ltlMainErrorsMessages.length = 0;
    ltlRailwayErrorsMessages.length = 0;
    ltlDowntimeErrorsMessages.length = 0;
    ltlPrrErrorsMessages.length = 0;
    ftlIntercityErrorsMessages.length = 0;
    ftlAdditionalErrorsMessages.length = 0;
    ftlMoscowErrorsMessages.length = 0;
    poolingErrorsMessages.length = 0;
  };

  const clearFormHandler = (form) => {
    switch (form) {
      case 'intercityFTL':
        setFtlIntercityData(ftlIntercityDataUtil);
        ftlIntercityErrorsMessages.length = 0;
        break;
      case 'additionalFTL':
        setFtlAdditionalData(ftlAdditionalDataUtil);
        ftlAdditionalErrorsMessages.length = 0;
        break;
      case 'moscowFTL':
        setFtlMoscowData(ftlMoscowDataUtil);
        ftlMoscowErrorsMessages.length = 0;
        break;
      case 'standardLTL':
        setLtlStandardData(ltlStandardDataUtil);
        ltlMainErrorsMessages.length = 0;
        break;
      case 'railwayLTL':
        setLtlRailwayData(ltlRailwayDataUtil);
        ltlRailwayErrorsMessages.length = 0;
        break;
      case 'downtimeLTL':
        setLtlDowntimeData(ltlDowntimeDataUtil);
        ltlDowntimeErrorsMessages.length = 0;
        break;
      case 'overweightLTL':
        setLtlOverweightData(ltlOverweightDataUtil);
        break;
      case 'prrLTL':
        setLtlPrrData(ltlPrrDataUtil);
        ltlPrrErrorsMessages.length = 0;
        break;
      case 'pooling':
        setPoolingData(poolingDataUtil);
        poolingErrorsMessages.length = 0;
        break;
      case 'downtimePooling':
        setPoolingDowntimeData(poolingDowntimeDataUtil);
        break;
      case 'overweightPooling':
        setPoolingOverweightData(poolingOverweightDataUtil);
        break;
      case 'additionalPooling':
        setPoolingAdditionalData(poolingAdditionalDataUtil);
        break;
    }
  };

  const sendDataRows = async (type) => {
    switch (type) {
      case 'ltl':
        generateLTL({
          type,
          commodityCode: commodityCode,
          clientName,
          email,
          dateFrom,
          dateTo,
          suppliers,
          ltlDelivery,
          ltlReturn,
          ltlReturnType,
          ltlStandardDataRef,
          ltlRailwayDataRef,
          ltlDowntimeDataRef,
          ltlOverweightDataRef,
          ltlPrrDataRef,
          comment: fileMessage,
          ltlSurchargeOversize: ltlSurchargeOversize
        })
        .then(response => {
          processingResponse(response);
        })
        .catch(error => {
          processingResponse(error);
        });
        setCommodityCode('');
        setLtlDelivery(false);
        setLtlReturn(false);
        setLtlReturnType(null);
        setLtlSurchargeOversize(false);
        break;
      case 'ftl':
        generateFTL({
          billing,
          type,
          clientName,
          email,
          dateFrom,
          dateTo,
          ftlAdditionalDataRef,
          ftlIntercityDataRef,
          ftlMoscowDataRef,
          comment: fileMessage
        })
        .then(response => {
          processingResponse(response);
        })
        .catch(error => {
          processingResponse(error);
        });
        break;
      case 'pooling':
        generatePooling({
          type,
          commodityCode: commodityCode,
          clientName,
          email,
          dateFrom,
          dateTo,
          suppliers,
          poolingDataRef,
          poolingDowntimeDataRef,
          poolingOverweightDataRef,
          poolingAdditionalDataRef,
          comment: fileMessage
        })
        .then(response => {
          processingResponse(response);
        })
        .catch(error => {
          processingResponse(error);
        });
        break;
      default:
        setToastMessage(`Произошла ошибка при отправке данных: неправильный тип ${type}`);
        setShowToast(true);
        break;
    }
    setShowModal(false);
    setDateFrom('');
    setDateTo('');
    setSuppliers('');
    setClientName('');
    setFileMessage('');
  };

  const processingResponse = (response) => {
    if (response.status === 200) {
      setToastMessage('Запрос на генерацию принят');
      setShowToast(true);
    } else {
      setToastMessage('Произошла ошибка при отправке данных');
      setShowToast(true);
    }
  };

  return (
    <Layout>
      <Logout>
        <Routes>
          <Route element={<PrivateRoute/>}>
            <Route path="/lsc-team/tariff-creator" element={<Navigate to="/lsc-team/tariff-creator/ltl"/>}/>
            <Route path="/lsc-team/tariff-creator/" element={<Navigate to="/lsc-team/tariff-creator/ltl"/>}/>
            <Route
              path="/lsc-team/tariff-creator/ltl"
              element={
                <LTLPage
                  showModal={showModal}
                  setShowModal={setShowModal}
                  hotRefStandard={ltlStandardDataRef}
                  hotRefRailway={ltlRailwayDataRef}
                  hotRefDowntime={ltlDowntimeDataRef}
                  hotRefOverweight={ltlOverweightDataRef}
                  hotRefPrr={ltlPrrDataRef}
                  warehouses={warehouses}
                  searchNames={searchNames}
                  standardData={ltlStandardData}
                  railwayData={ltlRailwayData}
                  downtimeData={ltlDowntimeData}
                  overweightData={ltlOverweightData}
                  prrData={ltlPrrData}
                  sendDataRows={sendDataRows}
                  clearAllHandler={clearAllHandler}
                  setCommodityCode={setCommodityCode}
                  setLtlDelivery={setLtlDelivery}
                  ltlReturn={ltlReturn}
                  setLtlReturn={setLtlReturn}
                  ltlReturnType={ltlReturnType}
                  setLtlReturnType={setLtlReturnType}
                  clearFormHandler={clearFormHandler}
                  setClientName={setClientName}
                  setDateFrom={setDateFrom}
                  setDateTo={setDateTo}
                  setSuppliers={setSuppliers}
                  ltlMainErrorsMessages={ltlMainErrorsMessages}
                  ltlRailwayErrorsMessages={ltlRailwayErrorsMessages}
                  ltlDowntimeErrorsMessages={ltlDowntimeErrorsMessages}
                  ltlPrrErrorMessages={ltlPrrErrorsMessages}
                  setShowErrorsModal={setShowErrorsModal}
                  setFileMessage={setFileMessage}
                  ltlSurchargeOversize={ltlSurchargeOversize}
                  setLtlSurchargeOversize={setLtlSurchargeOversize}
                />
              }/>
            <Route
              path="/lsc-team/tariff-creator/pooling"
              element={
                <PoolingPage
                  showModal={showModal}
                  setShowModal={setShowModal}
                  hotRefPooling={poolingDataRef}
                  downtimeDataRef={poolingDowntimeDataRef}
                  overweightDataRef={poolingOverweightDataRef}
                  additionalDataRef={poolingAdditionalDataRef}
                  poolingData={poolingData}
                  warehouses={warehouses}
                  searchNames={searchNames}
                  poolingDowntimeData={poolingDowntimeData}
                  poolingOverweightData={poolingOverweightData}
                  poolingAdditionalData={poolingAdditionalData}
                  sendDataRows={sendDataRows}
                  clearAllHandler={clearAllHandler}
                  setCommodityCode={setCommodityCode}
                  clearFormHandler={clearFormHandler}
                  setClientName={setClientName}
                  setDateFrom={setDateFrom}
                  setDateTo={setDateTo}
                  setSuppliers={setSuppliers}
                  setFileMessage={setFileMessage}
                  setShowErrorsModal={setShowErrorsModal}
                  poolingErrorsMessages={poolingErrorsMessages}
                  poolingDowntimeErrorsMessages={poolingDowntimeErrorsMessages}
                  poolingAdditionalErrorsMessages={poolingAdditionalErrorsMessages}
                />
              }/>
            <Route
              path="/lsc-team/tariff-creator/ftl"
              element={
                <FTLPage
                  showModal={showModal}
                  setShowModal={setShowModal}
                  hotRefIntercity={ftlIntercityDataRef}
                  hotRefAdditional={ftlAdditionalDataRef}
                  hotRefMoscow={ftlMoscowDataRef}
                  intercityData={ftlIntercityData}
                  additionalData={ftlAdditionalData}
                  moscowData={ftlMoscowData}
                  warehouses={warehouses}
                  billing={billing}
                  setBilling={setBilling}
                  sendDataRows={sendDataRows}
                  clearAllHandler={clearAllHandler}
                  clearFormHandler={clearFormHandler}
                  setClientName={setClientName}
                  setDateFrom={setDateFrom}
                  setDateTo={setDateTo}
                  setFileMessage={setFileMessage}
                  setShowErrorsModal={setShowErrorsModal}
                  ftlIntercityErrorsMessages={ftlIntercityErrorsMessages}
                  ftlAdditionalErrorsMessages={ftlAdditionalErrorsMessages}
                  ftlMoscowErrorsMessages={ftlMoscowErrorsMessages}
                />
              }/>
          </Route>
          <Route element={<AuthRoute/>}>
            <Route path="/lsc-team/tariff-creator/validation" element={<Validation/>}/>
          </Route>
          <Route path="/" element={<Navigate to="/lsc-team/tariff-creator/"/>}/>
          <Route path="/lsc-team/tariff-creator/reset" element={<ResetPassword/>}/>
          <Route path="/lsc-team/tariff-creator/changePassword" element={<ChangePassword/>}/>
          <Route path="/lsc-team/tariff-creator/login" element={<Login/>}/>
          <Route path="/lsc-team/tariff-creator/register" element={<Register/>}/>
          <Route path="*" element={<NotFound/>}/>
        </Routes>
      </Logout>
      <MessageToast
        show={showToast}
        setShow={setShowToast}
        message={toastMessage}
      />
      <ErrorsMessageModal
        show={showErrorsModal}
        setShow={setShowErrorsModal}
        ltlMainErrorsMessages={ltlMainErrorsMessages}
        ltlRailwayErrorsMessages={ltlRailwayErrorsMessages}
        ltlDowntimeErrorsMessages={ltlDowntimeErrorsMessages}
        ltlPrrErrorsMessages={ltlPrrErrorsMessages}
        ftlIntercityErrorsMessages={ftlIntercityErrorsMessages}
        ftlAdditionalErrorsMessages={ftlAdditionalErrorsMessages}
        ftlMoscowErrorsMessages={ftlMoscowErrorsMessages}
        poolingErrorsMessages={poolingErrorsMessages}
        poolingDowntimeErrorsMessages={poolingDowntimeErrorsMessages}
        poolingAdditionalErrorsMessages={poolingAdditionalErrorsMessages}
      />
    </Layout>
  );
}

export default App;
