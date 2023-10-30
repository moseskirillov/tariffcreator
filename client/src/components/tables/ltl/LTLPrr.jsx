import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { ruRU } from 'handsontable/i18n';

const LTLPrr = ({hotRef, data, colHeaders}) => {
  return (
    <HotTable
      rowHeaders={true}
      ref={hotRef}
      stretchH="all"
      colHeaders={colHeaders}
      contextMenu={[]}
      renderAllRows={false}
      className="htCenter"
      data={data}
      height="60vh"
      columns={[
        {type: 'numeric'}, {type: 'numeric'}, {type: 'numeric'}
      ]}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][3] !== null) {
            changes[i][3] = changes[i][3].trim();
          }
        }
      }}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
    />
  );
};

export default memo(LTLPrr);