import React, { useEffect, useState } from 'react';
import { Col, Nav, Row, Tab } from 'react-bootstrap';
import MessageToast from '../components/MessageToast';
import RejectModal from '../components/RejectModal';
import { instance } from '../Axios';
import TabPane from '../components/TabPane';
import SearchPanel from '../components/SearchPanel';

const Validation = () => {

  useEffect(() => {
    downloadFiles('tariffcreated', setAcceptFiles, setSearchedAcceptFiles);
  }, []);

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('Произошла ошибка');

  const [fileName, setFileName] = useState('');
  const [showRejectModal, setShowRejectModal] = useState(false);

  const [key, setKey] = useState('created');
  const [download, setDownload] = useState(false);

  const [acceptFiles, setAcceptFiles] = useState([{name: '', fullDate: '', date: '', comment: ''}]);
  const [searchedAcceptFiles, setSearchedAcceptFiles] = useState(acceptFiles);

  const [rejectedFiles, setRejectedFiles] = useState([{name: '', fullDate: '', date: '', comment: ''}]);
  const [searchedRejectedFiles, setSearchedRejectedFiles] = useState(rejectedFiles);

  const [archiveFiles, setArchiveFiles] = useState([{name: '', fullDate: '', date: '', comment: ''}]);
  const [searchedArchiveFiles, setSearchedArchiveFiles] = useState(archiveFiles);

  const [failedFiles, setFailedFiles] = useState([{name: '', fullDate: '', date: '', comment: ''}]);
  const [searchedFailedFiles, setSearchedFailedFiles] = useState(failedFiles);

  const downloadFiles = (bucketName, setDownloadFiles, setSearchedDownloadFiles) => {
    setDownload(true);
    instance.get('/files/download', {
      params: {bucketName: bucketName}
    })
    .then(response => {
      setFiles(response.data, setDownloadFiles, setSearchedDownloadFiles);
      setDownload(false);
    })
  }

  const setFiles = (data, setFiles, setSearchedFiles) => {
    sortAndSet(data.files, setFiles, setSearchedFiles);
  }

  const sortAndSet = (files, setStateFiles, setSearchedFiles) => {
    files.sort((a, b) => {
      const dateA = new Date(a.fullDate);
      const dateB = new Date(b.fullDate);
      if (dateA < dateB) {
        return 1;
      }
      if (dateA > dateB) {
        return -1;
      }
      return 0;
    });
    setStateFiles(files);
    setSearchedFiles(files);
  }

  const accept = (fileName) => {
    instance.post('/files/move', {
      fileName: fileName, fromBucketName: 'tariffcreated', toBucketName: 'tariffbucket'
    }).then(response => {
      if (response.status === 200) {
        setToastMessage('Файл передан на автопрогрузку')
        setShowToast(true);
        setSearchedAcceptFiles(searchedAcceptFiles.filter(e => e.name !== fileName))
      } else {
        setShowToast(true);
      }
    })
  }

  const reject = (fileName) => {
    setFileName(fileName);
    setShowRejectModal(true);
  }

  const handleReject = (email, file, reason, kind) => {
    instance.post('/files/reject', {
      email: email, file: fileName, reason: reason, kind: kind
    }).then(response => {
      setShowRejectModal(false);
      response.status === 200
        ? setToastMessage('Письмо успешно отправлено')
        : setToastMessage('Произошла ошибка');
      setShowToast(true);
    });
    setTimeout(() => {
      setSearchedAcceptFiles(searchedAcceptFiles.filter(e => e.name !== fileName))
    }, 1000)
  }

  const downloadFile = (fileName, bucketName) => {
    instance.get('/files/downloadFile', {
      params: {
        bucketName: bucketName, fileName: fileName
      },
      responseType: 'blob'
    }).then(response => {
      const href = URL.createObjectURL(response.data);
      const link = document.createElement('a');
      link.href = href;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(href);
    })
  }

  const search = (query) => {
    setSearchedAcceptFiles(acceptFiles
      .filter(element =>
        element.name.toLowerCase().includes(query.toLowerCase())
        || element.date.includes(query)
        || element.comment.toLowerCase().includes(query.toLowerCase())
      )
    );
  }

  return (
    <>
      <Tab.Container id="ltl-tabs" activeKey={key} onSelect={(key) => setKey(key)}>
        <Row className="gx-0 mt-3 mb-3">
          <Col>
            <Nav variant="pills" className="d-flex justify-content-center">
              <Nav.Item>
                <Nav.Link onClick={() => downloadFiles('tariffcreated', setAcceptFiles, setSearchedAcceptFiles)}
                          eventKey="created">Новые</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link onClick={() => downloadFiles('tariffrejected', setRejectedFiles, setSearchedRejectedFiles)}
                          eventKey="rejected">Отклоненные</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link onClick={() => downloadFiles('tariffarchive', setArchiveFiles, setSearchedArchiveFiles)}
                          eventKey="archive">Архив</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link onClick={() => downloadFiles('tarifffailed', setFailedFiles, setSearchedFailedFiles)}
                          eventKey="failed">Ошибка</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        <SearchPanel
          download={download}
          search={search}
        />
        {!download && <Row className="gx-0">
          <Col>
            <Tab.Content>
              <TabPane
                eventKey="created"
                files={searchedAcceptFiles}
                accept={accept}
                reject={reject}
                downloadFile={downloadFile}
                bucketName="tariffcreated"
              />
              <TabPane
                eventKey="rejected"
                files={searchedRejectedFiles}
                accept={accept}
                reject={reject}
                downloadFile={downloadFile}
                bucketName="tariffrejected"
              />
              <TabPane
                eventKey="archive"
                files={searchedArchiveFiles}
                accept={accept}
                reject={reject}
                downloadFile={downloadFile}
                bucketName="tariffarchive"
              />
              <TabPane
                eventKey="failed"
                files={searchedFailedFiles}
                accept={accept}
                reject={reject}
                downloadFile={downloadFile}
                bucketName="tarifffailed"
              />
            </Tab.Content>
          </Col>
        </Row>}
        <MessageToast
          show={showToast}
          setShow={setShowToast}
          message={toastMessage}
        />
        <RejectModal
          show={showRejectModal}
          setShow={setShowRejectModal}
          fileName={fileName}
          handleReject={handleReject}
        />
      </Tab.Container></>
  );
};

export default Validation;
