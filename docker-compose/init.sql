--
-- PostgreSQL database dump
--

--
-- Name: applications; Type: TABLE; Schema: public; Owner: analytics
--

CREATE TABLE applications (
    id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

--
-- Name: devices; Type: TABLE; Schema: public; Owner: analytics
--

CREATE TABLE devices (
    id             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id INT NOT NULL,
    model VARCHAR  NOT NULL,
    FOREIGN KEY (application_id) REFERENCES applications(id)
);

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: analytics
--

CREATE TABLE sessions (
    id              INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id  INT NOT NULL,
    device_id       INT NOT NULL,
    created_at      timestamp with time zone,
    FOREIGN KEY (application_id) REFERENCES applications(id),
    FOREIGN KEY (device_id) REFERENCES devices(id)
);


CREATE INDEX idx_sessions_created_at ON sessions USING btree (created_at);

--
-- Name: application_events; Type: TABLE; Schema: public; Owner: analytics
--

CREATE TABLE application_events (
    id                     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    session_id             INT NOT NULL,
    time_stamp             timestamp with time zone NOT NULL,
    application_event_type VARCHAR NOT NULL,
    payload jsonb,
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);



