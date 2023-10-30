import React from 'react';
import { Button, Container, Tab, Table } from 'react-bootstrap';

const TabPane = ({eventKey, files, accept, reject, downloadFile, bucketName}) => {
  return (
    <Tab.Pane eventKey={eventKey}>
      <Container style={{maxHeight: '500px'}} className="overflow-auto">
        <Table striped bordered hover >
          <thead>
          <tr>
            <th style={{textAlign: 'center'}}>#</th>
            <th style={{textAlign: 'center'}}>Дата создания</th>
            <th style={{textAlign: 'center'}}>Название</th>
            <th style={{textAlign: 'center'}}>Комментарий</th>
            {
              eventKey === 'created' &&
              <>
                <th></th>
              </>
            }
          </tr>
          </thead>
          <tbody>
          {files.map((element, index) =>
            <tr key={index}>
              <td style={{textAlign: 'center'}}>{index + 1}</td>
              <td style={{textAlign: 'center'}}>{element.date}</td>
              <td title={element.name}>
                <a style={{cursor: 'pointer'}}
                   onClick={() => downloadFile(element.name, bucketName)}
                >
                  {element.name.substring(0, 60)}{element.name.length > 60 ? ' ... ' : null}
                </a>
              </td>
              <td>{element.comment}</td>
              {
                eventKey === 'created' &&
                <>
                  <td style={{textAlign: 'center'}}>
                    <Button onClick={() => accept(element.name)} variant="outline-success">Подтвердить</Button>
                  </td>
                  <td style={{textAlign: 'center'}}>
                    <Button onClick={() => reject(element.name)} variant="outline-danger">Отклонить</Button>
                  </td>
                </>
              }
            </tr>
          )}
          </tbody>
        </Table>
      </Container>
    </Tab.Pane>
  );
};

export default TabPane;