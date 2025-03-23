-- Insert Initial Request Statuses
INSERT INTO statuses (name, description) VALUES
('DRAFT', 'Request is in draft mode'),
('SUBMITTED', 'Request has been submitted'),
('IN_PROGRESS', 'Request is in progress'),
('DONE', 'Request has been approved'),
('CANCELLED', 'Request has been rejected');

-- Insert Initial Attachment Types
INSERT INTO ATTACHMENT_TYPES (name, description) VALUES
('PASSPORT', 'Passport document attachment'),
('ID_CARD', 'National ID card attachment'),
('DRIVING_LICENSE', 'Driving license attachment'),
('UTILITY_BILL', 'Utility bill as proof of address'),
('BANK_STATEMENT', 'Bank statement attachment');
