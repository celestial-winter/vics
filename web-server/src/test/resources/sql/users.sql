-- passwords are same as username
INSERT INTO users (id, first_name, last_name, write_access, username, password_hash, role) VALUES
  ('a54a4e73-943d-41e0-ae05-f6b507ad777e', 'iam', 'sam', true, 'me@admin.uk', '$2a$10$0qF8dDUyjN8Pc.AkS4twGuG/9siJzjNdIhMCphdY.Su7/XZLmDbW.', 'ADMIN'),
  ('63f93970-d065-4fbb-8b9c-941e27ea53dc', 'Peter', 'Ndlovu', true, 'cov@south.cov', '$2a$10$JDT0Ne/x8dzbk66tI5zN7eHFf9Xn0KiO3.T0RfHwIoujY9VF.SD.u', 'USER'),
  ('ccda23ab-a380-4fa8-9f83-a77fca86dd6f', 'Ava', 'McCall', true, 'avamcall@voteleave.uk', '$2a$10$JDT0Ne/x8dzbk66tI5zN7eHFf9Xn0KiO3.T0RfHwIoujY9VF.SD.u', 'USER'),
  ('556febe0-7052-4805-a0dc-9e4603e46095', 'Martin', 'Freeman', true, 'mf@mfsd.uk', '$2a$10$JDT0Ne/x8dzbk66tI5zN7eHFf9Xn0KiO3.T0RfHwIoujY9VF.SD.u', 'USER'),
  ('0c9b160d-f178-4d40-ae21-94ba78ab748f', 'Samir', 'Ginola', true, 'ada@ada.uk', '$2a$10$JDT0Ne/x8dzbk66tI5zN7eHFf9Xn0KiO3.T0RfHwIoujY9VF.SD.u', 'USER'),
  ('f04dbf90-57f3-4925-b9f0-a67cf32858db', 'Kelly', 'Alderon', true, 'kalderon@voteleave.uk', '$2a$10$JDT0Ne/x8dzbk66tI5zN7eHFf9Xn0KiO3.T0RfHwIoujY9VF.SD.u', 'USER'),
  ('c48f187f-ef5f-4a0d-a42b-206eec26aed3', 'Me', 'You', true, 'me.you@voteleave.uk', '$2a$10$zPIZ9mXPowyc5LV/ehdRue0oX5nf10hxWDJ0SdpbpuOZZdR0IDEEy', 'USER'),
  ('196af608-6d7a-4981-a6a0-ed8999b3b89c', 'Dion', 'Dublin', true, 'earlsdon@cov.uk', '$2a$10$QzFIYoX2Ha8AzDFrztJys.A1.hjDa7dpgpbk.o4pq3xJ3dgjGgqly', 'USER');

INSERT INTO privileges (id, permission) VALUES
  ('3f89506c-fd00-4b1e-aefc-2186d075439d', 'READ_VOTER'),
  ('5044556a-e90b-4bd3-b1e1-27f43e9a405f', 'EDIT_VOTER'),
  ('a54a4e73-943d-41e0-ae05-f6b507ad777e', 'VIEW_MAPS');

INSERT INTO users_privileges (users_id, privileges_id) VALUES
  -- covs READ/WRITE
  ('63f93970-d065-4fbb-8b9c-941e27ea53dc', '3f89506c-fd00-4b1e-aefc-2186d075439d'),
  ('63f93970-d065-4fbb-8b9c-941e27ea53dc', '5044556a-e90b-4bd3-b1e1-27f43e9a405f'),

  -- admin view maps
  ('a54a4e73-943d-41e0-ae05-f6b507ad777e', 'a54a4e73-943d-41e0-ae05-f6b507ad777e'),

  -- earlsdon READ ONLY
  ('196af608-6d7a-4981-a6a0-ed8999b3b89c', '3f89506c-fd00-4b1e-aefc-2186d075439d');

INSERT INTO users_constituencies (users_id, constituencies_id) VALUES
  -- covs -> covs const
  ('63f93970-d065-4fbb-8b9c-941e27ea53dc', '0d338b99-3d15-44f7-904f-3ebc18a7ab4a');

INSERT INTO users_wards (users_id, wards_id) VALUES
  -- earlsdon -> earlsdon ward
  ('196af608-6d7a-4981-a6a0-ed8999b3b89c', '585aedd2-6f46-4718-854b-1856a3b1f3c1'),
  ('63f93970-d065-4fbb-8b9c-941e27ea53dc', '3c0d5cdc-c142-4155-9e0e-9719b9bd5d79'),

-- admin
  ('a54a4e73-943d-41e0-ae05-f6b507ad777e', '52c59288-fc44-4b72-bca8-bc6c06416613'),
  ('a54a4e73-943d-41e0-ae05-f6b507ad777e', '395b4d40-5074-4f2c-9c68-ea352f909402');
