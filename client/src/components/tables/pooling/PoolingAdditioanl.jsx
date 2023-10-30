import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { cities } from '../../../util/tariffsPooling';
import { ruRU } from 'handsontable/i18n';
import replaceCityName from '../../../util/functions';

const PoolingAdditional = ({ hotRef, data, colHeaders, warehouses }) => {
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
      columns={[
        { type: 'dropdown', source: warehouses, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: cities, allowInvalid: true, validator: 'dropdown' },
        { type: 'dropdown', source: ['реф', 'изо', 'тент'], allowInvalid: true, validator: 'dropdown' },
        { type: 'numeric' },
        { type: 'numeric' },
        { type: 'numeric' },
        { type: 'numeric' },
        { type: 'numeric' }
      ]}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 4
            || changes[i][1] === 5
            || changes[i][1] === 6
            || changes[i][1] === 7) {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].replace(/\s/g, '');
            }
          } else if (changes[i][1] === 1) {
            changes[i][3] = replaceCityName(changes[i][3]);
          } else if (changes[i][1] === 2) {
            changes[i][3] = changes[i][3].toLowerCase().trim();
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

export default memo(PoolingAdditional);