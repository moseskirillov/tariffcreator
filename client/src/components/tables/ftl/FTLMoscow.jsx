import React, { memo } from 'react';
import { HotTable } from '@handsontable/react';
import { ruRU } from 'handsontable/i18n';

const FtlMoscow = (
  {
    hotRef,
    data,
    warehouses,
    nestedHeaders
  }) => {

  return (
    <HotTable
      ref={hotRef}
      data={data}
      height="60vh"
      stretchH="all"
      colHeaders={true}
      rowHeaders={true}
      contextMenu={true}
      className="htCenter"
      renderAllRows={false}
      columnHeaderHeight={36}
      nestedHeaders={nestedHeaders}
      beforeChange={(changes) => {
        for (let i = 0; i < changes.length; i++) {
          if (changes[i][1] === 5
            || changes[i][1] === 6
            || changes[i][1] === 7
            || changes[i][1] === 8
            || changes[i][1] === 9
            || changes[i][1] === 10
            || changes[i][1] === 11
            || changes[i][1] === 12) {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].replace(/\s/g, '');
            }
          } else if (changes[i][1] === 3) {
            changes[i][3] = changes[i][3].toLowerCase().trim();
          } else {
            if (changes[i][3] !== null) {
              changes[i][3] = changes[i][3].trim();
            }
          }
        }
      }}
      columns={[
        {
          type: 'dropdown',
          source: warehouses,
          allowInvalid: true,
          validator: 'dropdown',
          allowEmpty: false
        },
        {
          type: 'dropdown',
          source: ['1.5т', '3т', '5т', '10т', '20т'],
          allowInvalid: true,
          validator: 'dropdown',
          allowEmpty: false
        },
        {
          type: 'dropdown',
          source: ['1-4', '5-6', '7-8', '9-15', '16-33'],
          allowInvalid: true,
          validator: 'dropdown',
          allowEmpty: false
        },
        {
          type: 'dropdown',
          source: ['изо', 'реф', 'тент'],
          allowInvalid: true,
          validator: 'dropdown',
          allowEmpty: false,
        },
        { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }, { type: 'numeric' }
      ]}
      language={ruRU.languageCode}
      licenseKey="non-commercial-and-evaluation"
      colWidths={[40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40]}
    />
  );
};

export default memo(FtlMoscow);