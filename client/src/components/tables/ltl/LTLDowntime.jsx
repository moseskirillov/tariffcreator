import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { ruRU } from "handsontable/i18n";

const LtlDowntime = ({hotRef, data, nestedHeaders, columns}) => {
  return (
    <HotTable
      ref={hotRef}
      stretchH="all"
      colHeaders={true}
      rowHeaders={true}
      contextMenu={[]}
      renderAllRows={false}
      height="60vh"
      className="htCenter"
      data={data}
      columnHeaderHeight={36}
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
          }
        }
      }}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
    />
  );
};

export default memo(LtlDowntime);