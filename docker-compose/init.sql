

CREATE TABLE aggregatedRecords (
    id                        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id            VARCHAR NOT NULL
    timeWindowStart           timestamp with time zone NOT NULL,
    averageSessionsPerDevice  INT NOT NULL,
    totalSessions             INT NOT NULL,
    activeDevices             INT NOT NULL,
    timeWindowInSeconds       INT NOT NULL
);

CREATE INDEX idx_aggregatedRecords_timeWindowStart ON aggregatedRecords USING btree (timeWindowStart);
CREATE INDEX idx_aggregatedRecords_application_id ON aggregatedRecords USING btree (application_id);
