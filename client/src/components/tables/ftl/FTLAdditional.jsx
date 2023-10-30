import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { ruRU } from "handsontable/i18n";

const FTLAdditional = ({hotRef, data, colHeaders}) => {
  return (
    <HotTable
      rowHeaders={true}
      ref={hotRef}
      stretchH="all"
      colHeaders={colHeaders}
      contextMenu={true}
      className="htCenter"
      data={data}
      height="60vh"
      renderAllRows={false}
      columns={[
        {type: 'dropdown', source: ['Moscow', 'Region']},
        {type: 'dropdown', source: ['Moscow', 'Region']},
        {type: 'dropdown', source: ['1.5т', '3т', '5т', '10т', '20т']},
        {
          type: 'dropdown', source: [
            'Стоимость за каждые сутки простоя транспортного средства при превышении первых 24 часов работы',
            'Дополнительная точка выгрузки в городе',
            'Дополнительная точка выгрузки по пути следования, но не более 100 км от маршрута',
            'Экспедирование водителем (покоробочная приемка и сдача груза)',
            'Экспедитор ФМ Ложистик на загрузке/выгрузке'
          ]
        }, {type: 'numeric'}, {type: 'numeric'}, {type: 'numeric'}
      ]}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 4
            || changes[i][1] === 5
            || changes[i][1] === 6) {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].replace(/\s/g, '');
            }
          } else {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].trim();
            }
          }
        }
      }}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
    />
  );
};

export default memo(FTLAdditional);