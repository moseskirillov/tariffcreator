package com.fmlogistic.tariffcreator.adapter;

import com.fmlogistic.tariffcreator.models.generator.request.BaseRequest;
import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;
import com.fmlogistic.tariffcreator.models.generator.request.LTLRequest;
import com.fmlogistic.tariffcreator.models.generator.request.PoolingRequest;
import com.fmlogistic.tariffcreator.services.forms.FTLService;
import com.fmlogistic.tariffcreator.services.forms.LTLService;
import com.fmlogistic.tariffcreator.services.forms.PoolingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratorAdapterImpl implements GeneratorAdapter {

    private static final String LTL = "LTL";
    private static final String FTL = "FTL";
    private static final String POOLING = "Pooling";

    private static final String SELECT_REQUEST_TYPE = "Определен тип запроса: %s";

    private final LTLService ltlService;
    private final FTLService ftlService;
    private final PoolingService poolingService;

    @Override
    public <T extends BaseRequest> void generate(T model) {
        generateCurrent(model);
    }

    private <T extends BaseRequest> void generateCurrent(T model) {
        if (model instanceof LTLRequest) {
            log.info(String.format(SELECT_REQUEST_TYPE, LTL));
            ltlService.generate((LTLRequest) model);
        } else if (model instanceof FTLRequest) {
            log.info(String.format(SELECT_REQUEST_TYPE, FTL));
            ftlService.generate((FTLRequest) model);
        } else if (model instanceof PoolingRequest) {
            log.info(String.format(SELECT_REQUEST_TYPE, POOLING));
            poolingService.generate((PoolingRequest) model);
        } else {
            throw new RuntimeException();
        }
    }
}
