import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { ruRU } from "handsontable/i18n";

const PoolingDowntime = ({hotRef, data, nestedHeaders, columns}) => {
  return (
    <HotTable
      rowHeaders={true}
      columnHeaderHeight={36}
      ref={hotRef}
      stretchH="last"
      colHeaders={true}
      contextMenu={['remove_row']}
      renderAllRows={false}
      className="htCenter"
      data={data}
      height="60vh"
      nestedHeaders={nestedHeaders}
      columns={columns}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 3
            || changes[i][1] === 4
            || changes[i][1] === 5) {
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

export default memo(PoolingDowntime);