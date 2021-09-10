package ru.detmir.metrics.service;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>Библиотека метрик</h1>
 * В организации создана небольшая библиотека для измерения скорости работы кода, сохранения и чтения этих данных в
 * клиентском хранилище. <p/>
 *
 * Библиотека в настоящий момент используется в нескольких проектах разными командами, и за время использования у
 * разработчиков появился беклог по этой библиотеке. Кроме того, бизнес прогнозирует кратное увеличение нагрузки на
 * основные сервисы компании в течение года, и тимлид просит подумать насчет работы под высокой нагрузкой и
 * отказоустойчивости библиотеки. <p/>
 *
 * Необходимо решить задачи из беклога и предложить несколько улучшений библиотеки на своё усмотрение.<p/>
 *
 * <h4>Беклог:</h4> <ul>
 * <li>Команда А пишет, что иногда в хранилище попадают измерения с длительностью = 0, просят починить.</li>
 * <li>Команда Б жалуется, что их хранилище не очень надежное и им нужен асинхронный режим записи данных.</li>
 * <li>Команда В хочет, чтобы библиотеку можно было использовать для сохранения других метрик, например - http-кодов ответов.</li>
 * <li>Команда В также хочет передавать свои произвольные поля в хранилище, например "METHOD->POST" или "EXCEPTION->NPE"</li>
 * </ul>
 */
public class MetricService {

    private MeasureStorage measureStorage;
    private ConcurrentHashMap<String, String> longestExecutions = new ConcurrentHashMap<>();

    @Value("${detmir.appName:app}")
    private String appName;
    @Value("${detmir.hostname:dev}")
    private String hostname;
    @Value("${detmir.environment:dev}")
    private String environment;

    public MetricService(MeasureStorage measureStorage) {
        this.measureStorage = measureStorage;
    }

    public void measure(String name, Runnable r) {
        Date dateFrom = new Date();
        Date dateTo = new Date();

        Measure measure = new Measure();
        measure.metricName = name;
        measure.metricDate = dateFrom;
        measure.customFields = Map.of(
                "application", appName,
                "environment", environment,
                "host", hostname
        );
        try {
            dateFrom = new Date();
            r.run();
            dateTo = new Date();
        } finally {
            long execTime = dateTo.getTime() - dateFrom.getTime();
            measure.metricValue = String.valueOf(execTime);
            String id = measureStorage.sendMeasure(measure);
            changeLongest(name, id, execTime);
        }
    }

    public Measure getLongestMeasure(String name) {
        String longestId = longestExecutions.get(name);
        if (longestId == null) {
            return null;
        } else {
            return measureStorage.readMeasure(longestId);
        }
    }

    private void changeLongest(String name, String id, long execTime) {
        String longestMeasureId = longestExecutions.get(name);
        boolean replaceMaxId = false;
        if (longestMeasureId == null) {
            replaceMaxId = true;
        } else {
            Measure oldMeasure = measureStorage.readMeasure(longestMeasureId);
            if (Long.parseLong(oldMeasure.metricValue) < execTime) {
                replaceMaxId = true;
            }
        }

        if (replaceMaxId) {
            longestExecutions.put(name, id);
        }
    }

    public class Measure {
        public Date metricDate;
        public String metricName;
        public String metricValue;
        public Map<String, String> customFields;
    }

    public interface MeasureStorage {
        /**
         * send measure to some storage and return it newly created ID
         */
        String sendMeasure(Measure measure);

        /**
         * read measure from storage by ID
         */
        Measure readMeasure(String id);

        /**
         * send list of measures to storage
         */
        void sendMeasures(List<Measure> measures);
    }

    public class Example {

        MetricService metricService;

        public Example(MetricService metricService) {
            this.metricService = metricService;
        }


        public void test() {
            metricService.measure("update_users_db_query", () -> {
                // jdbcTemplate.execute(...
            });

            metricService.measure("delete_user_db_query", () -> {
                // jdbcTemplate.execute(...
            });

            Measure measure = metricService.getLongestMeasure("update_usres_db_query");
            if (Long.parseLong(measure.metricValue) > 1500L) {
                // logger.warn("got troubles with query update_users_db_query")
            }


        }
    }
}
