ALTER TABLE environment_info
    ADD identity_measured_date DATE
        GENERATED ALWAYS AS (COALESCE(measured_date, DATE '1000-01-01')) STORED,
    ADD UNIQUE KEY uq_environment_info_identity (
        item_name,
        identity_measured_date,
        latitude,
        longitude
    );
