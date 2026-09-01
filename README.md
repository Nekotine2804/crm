README.md

## Diagrams

- [System Architecture, Use Case, Sequence, Activity, DFD và ERD](docs/diagrams/system-diagrams.md)
- [Loyalty flow và tier evaluation](docs/diagrams/loyalty-flow.md)


POC Loyalty & CRM Bán lẻ
1. Mục tiêu dự án
   Xây dựng một Proof of Concept (POC) cho hệ thống Loyalty & CRM trong lĩnh vực bán lẻ, tập trung vào khả năng phân tích nghiệp vụ, đặc tả yêu cầu, thiết kế kiến trúc/CSDL/API, mô hình hóa luồng nghiệp vụ, xây dựng wireframe và triển khai mã nguồn minh họa cho chức năng cốt lõi.

Lưu ý: Đây là POC phục vụ đánh giá năng lực phân tích, thiết kế và triển khai. Không yêu cầu hệ thống production-ready hoặc tích hợp thực tế với POS.

2. Mục tiêu chính
   Nghiên cứu mô hình Loyalty/CRM bán lẻ.

Xác định actor, nghiệp vụ và các luồng xử lý cốt lõi.

Xây dựng BRD và SRS đầy đủ, có mã yêu cầu.

Đề xuất kiến trúc tổng thể có khả năng tích hợp POS.

Thiết kế CSDL/ERD cho Customer, Loyalty, Transaction và các thực thể liên quan.

Thiết kế API cho các chức năng cốt lõi.

Mô hình hóa Use Case, Sequence, Activity và DFD.

Xây dựng wireframe tối thiểu 3 màn hình chính.

Xây dựng POC chạy được.

Viết tối thiểu 10 test case gồm happy path và exception cases.

Tài liệu hóa Risk, Assumption và Decision Log.

Chuẩn bị presentation tối đa 20 slide.

3. Phạm vi dự án
   3.1. In-scope
   Phân tích nghiệp vụ Loyalty & CRM.

Phân tích actor và stakeholder.

Phân tích các luồng nghiệp vụ cốt lõi.

Xác định business rules, constraints và exception cases.

Xây dựng Functional Requirements và Non-functional Requirements.

Phân loại ưu tiên MVP / Phase 2 / Phase 3.

Đề xuất kiến trúc tổng thể.

Thiết kế CSDL và ERD.

Thiết kế API tích hợp POS.

Mô hình hóa Process / Sequence / Use Case / Activity / DFD.

Xây dựng Wireframe.

Xây dựng POC chức năng cốt lõi.

Viết test case.

Tài liệu hóa Risk / Assumption / Decision.

Presentation.

3.2. Out-of-scope
Triển khai production-ready.

Tích hợp thực tế với POS thật.

UI high-fidelity.

Xây dựng production infrastructure.

Tài liệu vận hành production.

Đàm phán hợp đồng hoặc chi phí thương mại.

4. Định hướng nghiệp vụ
   4.1. Mô hình Loyalty đề xuất
   Hệ thống quản lý khách hàng thành viên và tích điểm dựa trên giao dịch mua hàng.

Luồng cơ bản:

POS
|
| Transaction
v
Loyalty/CRM
|
+--> Xác định Customer
|
+--> Kiểm tra điều kiện Loyalty
|
+--> Tính điểm
|
+--> Cập nhật Point Balance
|
+--> Cập nhật Tier nếu đủ điều kiện
|
+--> Ghi nhận Point Transaction
|
v
Response về POS
4.2. Công thức tích điểm
Công thức POC:

earnedPoint = transactionAmount × tier.pointMultiplier
Ví dụ:

transactionAmount = 1,000,000 VND
tier.pointMultiplier = 0.01

earnedPoint = 1,000,000 × 0.01
= 10,000 points
Trong implementation cần thống nhất rõ đơn vị của pointMultiplier.

Khuyến nghị có thể quy đổi theo đơn vị tiền:

earnedPoint = floor(transactionAmount / pointUnit) × pointRate × tierMultiplier
Ví dụ:

pointUnit = 10,000 VND
pointRate = 1 point
tierMultiplier = 1.5

transactionAmount = 1,000,000

basePoint = 1,000,000 / 10,000 = 100
earnedPoint = 100 × 1 × 1.5 = 150 points
Không tự ý thay đổi business rule. Nếu chọn công thức khác phải ghi vào docs/assumptions/ và docs/decision-log/.

5. Actor
   Tối thiểu xem xét các actor sau:

Actor	Vai trò
Customer	Khách hàng thành viên, tích điểm và sử dụng quyền lợi
POS	Gửi giao dịch mua hàng sang Loyalty
Staff	Tra cứu khách hàng, hỗ trợ đăng ký/điều chỉnh nghiệp vụ
CRM Admin	Quản lý Customer, Tier, Loyalty Rule
System	Tự động tính điểm, cập nhật tier, ghi nhận lịch sử
Notification Service	Gửi thông báo khi cần
Có thể bổ sung actor nếu phân tích nghiệp vụ phát hiện nhu cầu.

6. Chức năng cốt lõi của MVP
   Ưu tiên POC các chức năng:

F01 - Customer
Tạo customer.

Tra cứu customer.

Cập nhật thông tin customer.

Xem loyalty profile.

F02 - Loyalty Account
Tạo loyalty account.

Xem point balance.

Xem point transaction history.

F03 - Earn Point
Nhận transaction từ POS và:

Validate request.

Xác định customer.

Kiểm tra transaction có hợp lệ không.

Xác định tier hiện tại.

Tính earned point.

Cập nhật balance.

Ghi point transaction.

Trả kết quả cho POS.

F04 - Tier
Xác định tier.

Xem tier hiện tại.

Tính điều kiện nâng tier.

Cập nhật tier khi đủ điều kiện.

F05 - Transaction
Nhận transaction từ POS.

Chống xử lý transaction trùng.

Lưu transaction.

Liên kết transaction với customer.

7. Business Rules cần phân tích
   Các rule tối thiểu:

Customer phải tồn tại trước khi tích điểm, hoặc phải xác định rõ cơ chế guest transaction.

Transaction phải có unique transaction ID.

Không được cộng điểm hai lần cho cùng một transaction.

Transaction bị cancel/refund phải có cơ chế reverse point.

Điểm không được âm nếu business rule không cho phép.

Tier được xác định theo rule đã cấu hình.

Point transaction phải có audit trail.

Mọi thay đổi balance phải có lý do/source.

Request từ POS phải được authentication/authorization ở mức phù hợp với POC.

Cần xác định xử lý retry/idempotency khi POS gửi lại request.

8. Deliverables
   Cấu trúc thư mục tài liệu đề xuất:

docs/
├── 01-business-analysis/
│   └── business-analysis.md
├── 02-brd/
│   └── brd.md
├── 03-srs/
│   └── srs.md
├── 04-architecture/
│   └── architecture.md
├── 05-database/
│   ├── database-design.md
│   └── erd/
├── 06-api/
│   └── api-specification.md
├── 07-diagrams/
│   ├── use-case/
│   ├── sequence/
│   ├── activity/
│   └── dfd/
├── 08-wireframe/
│   └── wireframe.md
├── 09-test-case/
│   └── test-case.md
├── 10-risk/
│   └── risk-assessment.md
├── 11-assumptions/
│   └── assumptions.md
├── 12-decision-log/
│   └── decision-log.md
└── 13-presentation/
└── presentation.md
9. Danh mục Deliverables bắt buộc
   ID	Deliverable	Yêu cầu tối thiểu
   4.1	Business Analysis Document	Actor, luồng nghiệp vụ chính, vấn đề nghiệp vụ
   4.2	BRD	Mục tiêu, phạm vi, stakeholder, success criteria
   4.3	SRS	FR có mã số, NFR, priority
   4.4	Architecture Proposal	Kiến trúc tổng thể + thành phần + data flow
   4.5	Database Design / ERD	Entity, attribute, relationship
   4.6	API Specification	Endpoint, method, input/output
   4.7	Diagrams	Use Case + Sequence + Activity tối thiểu
   4.8	DFD	Source/destination/process/data flow
   4.9	Prototype / Wireframe	Tối thiểu 3 màn hình
   4.10	Source Code / POC	Chạy được + README setup
   4.11	Test Case	Tối thiểu 10 case
   4.12	Risk Assessment	Impact, probability, mitigation
   4.13	Assumption Document	Assumption + lý do
   4.14	Decision Log	Tối thiểu 5 quyết định quan trọng
   4.15	Presentation	Tối đa 20 slide
10. Kiến trúc đề xuất
    Có thể sử dụng kiến trúc modular hoặc microservice tùy quy mô POC.

Kiến trúc logic:

                 +----------------+
                 |      POS       |
                 +-------+--------+
                         |
                         | REST API
                         v
                 +-------+--------+
                 | API / Gateway  |
                 +-------+--------+
                         |
             +-----------+-----------+
             |                       |
             v                       v
      +------+-------+       +-------+------+
      | Loyalty      |       | Customer     |
      | Service      |       | Service      |
      +------+-------+       +-------+------+
             |                       |
             +-----------+-----------+
                         |
                         v
                  +------+------+
                  | PostgreSQL  |
                  +-------------+
Đối với POC, không over-engineer. Nếu microservice làm tăng độ phức tạp nhưng không chứng minh được giá trị, có thể sử dụng modular monolith và ghi rõ lý do trong Decision Log.

11. CSDL định hướng
    Các entity tối thiểu:

Customer
CustomerProfile
LoyaltyAccount
LoyaltyTier
LoyaltyRule
Transaction
PointTransaction
Quan hệ dự kiến:

Customer 1 --- 1 LoyaltyAccount
LoyaltyTier 1 --- N LoyaltyAccount
Customer 1 --- N Transaction
Transaction 1 --- N PointTransaction
LoyaltyAccount 1 --- N PointTransaction
LoyaltyTier 1 --- N LoyaltyRule
Cần xem xét thêm:

Unique constraint.

Foreign key.

Index.

Audit fields.

CreatedAt / UpdatedAt.

Optimistic locking nếu cần.

Idempotency key / external transaction ID.

Decimal precision cho monetary value.

Integer/Long/BigDecimal phù hợp cho point và tiền.

12. API tối thiểu
    API cần được đặc tả trong docs/06-api/api-specification.md.

Ví dụ:

POST /api/v1/customers
GET  /api/v1/customers/{customerId}
GET  /api/v1/customers/{customerId}/loyalty
GET  /api/v1/customers/{customerId}/points
POST /api/v1/loyalty/earn
POST /api/v1/transactions
GET  /api/v1/transactions/{transactionId}
API earn point cần chú ý:

{
"transactionId": "POS-20260901-000001",
"customerId": "CUS-001",
"transactionAmount": 1000000,
"transactionTime": "2026-09-01T09:00:00+07:00"
}
Response mẫu:

{
"transactionId": "POS-20260901-000001",
"customerId": "CUS-001",
"earnedPoints": 150,
"balance": 1250,
"tier": "GOLD"
}
Đây chỉ là contract mẫu. Cần hoàn thiện validation, error response và HTTP status code trong API Specification.

13. Idempotency
    Đây là vấn đề quan trọng khi tích hợp POS.

Ví dụ POS gửi:

POST /api/v1/loyalty/earn
transactionId = POS-20260901-000001
Nếu request timeout và POS retry:

POST /api/v1/loyalty/earn
transactionId = POS-20260901-000001
Hệ thống không được cộng điểm lần thứ hai.

POC phải chứng minh được ít nhất một cơ chế:

Unique transactionId
+
Database unique constraint
+
Idempotent business processing
14. Exception Cases
    Tối thiểu phải phân tích:

Customer không tồn tại.

Transaction amount <= 0.

Transaction ID bị trùng.

Transaction đã được xử lý.

Tier không tồn tại.

Loyalty rule không tồn tại.

Database error.

POS gửi request thiếu field.

POS retry request.

Refund/cancel transaction.

Customer bị inactive.

Điểm tính ra không hợp lệ.

15. Functional Requirement Convention
    Đặt mã requirement theo format:

FR-CUS-001
FR-LOY-001
FR-TXN-001
FR-TIER-001
FR-API-001
Ví dụ:

FR-LOY-001:
System SHALL calculate earned points when a valid purchase
transaction is received from POS.
Mỗi FR nên có:

ID

Name

Description

Actor

Pre-condition

Main flow

Alternative flow

Exception

Acceptance criteria

Priority

16. Priority
    Sử dụng:

P0 = Must have / MVP
P1 = Should have
P2 = Could have
P3 = Future
MVP nên tập trung:

Customer
↓
Transaction
↓
Earn Point
↓
Point Balance
↓
Point History
↓
Tier
Không triển khai quá nhiều tính năng CRM nâng cao nếu không cần thiết cho POC.

17. Non-functional Requirements
    Tối thiểu xem xét:

Performance
API core response time mục tiêu.

Database query có index phù hợp.

Không load toàn bộ point history nếu dữ liệu lớn.

Security
Authentication.

Authorization.

Input validation.

Không log thông tin nhạy cảm.

Không lưu plaintext password nếu có authentication.

Reliability
Idempotency.

Transaction consistency.

Retry handling.

Maintainability
Layered/modular architecture.

Clear naming.

Separation of concerns.

Unit test cho business logic.

Observability
Structured logging.

Request ID / correlation ID.

Error logging.

18. POC Implementation
    POC phải:

Build được.

Run được local.

Có README hướng dẫn.

Có database setup/migration.

Có API documentation hoặc Postman collection.

Có sample data.

Có test.

Có thể demo happy path và exception path.

Nếu sử dụng Java/Spring Boot:

Controller
↓
Application Service
↓
Domain / Business Logic
↓
Repository
↓
Database
Không để business logic quan trọng nằm trực tiếp trong Controller.

19. Test Case
    Tối thiểu 10 test case.

Khuyến nghị:

ID	Scenario	Expected
TC-001	Earn point hợp lệ	Cộng điểm thành công
TC-002	Customer không tồn tại	404 / business error
TC-003	Amount = 0	Validation error
TC-004	Amount âm	Validation error
TC-005	Duplicate transaction	Không cộng điểm lần 2
TC-006	Retry cùng transaction	Idempotent
TC-007	Tier không tồn tại	Business error
TC-008	Refund	Reverse point đúng rule
TC-009	Customer inactive	Không cho tích điểm
TC-010	Database failure	Không tạo trạng thái điểm không nhất quán
Có thể bổ sung test cho:

Boundary amount.

Tier upgrade.

Concurrent requests.

Invalid JSON.

Missing required fields.

20. Risk Assessment
    Risk cần có:

Risk
Impact
Probability
Severity
Mitigation
Contingency
Owner
Các risk quan trọng:

POS contract không rõ.

Transaction duplicate.

Concurrent point update.

Business rule thay đổi.

Data inconsistency.

Sai công thức tích điểm.

Scope creep.

Over-engineering kiến trúc.

Thiếu dữ liệu production thực tế.

21. Assumption Document
    Mọi giả định nghiệp vụ phải ghi rõ.

Ví dụ:

A-001
Assumption:
POS cung cấp transactionId duy nhất.

Reason:
Cần transactionId để đảm bảo idempotency.

Impact:
Nếu POS không đảm bảo unique transactionId,
cần thêm cơ chế idempotency key hoặc mapping.
Không được biến assumption thành fact mà không ghi nhận.

22. Decision Log
    Tối thiểu 5 quyết định.

Format:

Decision ID
Date
Context
Problem
Options
Decision
Reason
Trade-off
Impact
Ví dụ các quyết định:

Chọn Modular Monolith hay Microservices.

Chọn PostgreSQL.

Dùng transactionId để idempotency.

Cách tính earned point.

Cách quản lý Tier.

Cách xử lý refund.

REST API cho POS integration.

23. Wireframe
    Tối thiểu 3 màn hình, đại diện cho các nhóm người dùng.

Khuyến nghị:

Screen 1 - Customer Loyalty Profile
Hiển thị:

Customer information.

Current tier.

Point balance.

Progress đến tier tiếp theo.

Point transaction history.

Screen 2 - Staff / CRM Customer Management
Hiển thị:

Customer search.

Customer list.

Customer detail.

Loyalty information.

Screen 3 - Admin Loyalty Configuration
Hiển thị:

Tier.

Point multiplier/rule.

Rule status.

Effective date.

Wireframe chỉ cần low-fidelity, không cần UI production/high-fidelity.

24. Diagram Convention
    Có thể sử dụng Mermaid để dễ quản lý trong Git.

Ví dụ:

sequenceDiagram
participant POS
participant API
participant Loyalty
participant DB

    POS->>API: POST /loyalty/earn
    API->>Loyalty: Process transaction
    Loyalty->>DB: Check transaction
    DB-->>Loyalty: Transaction status
    Loyalty->>DB: Calculate & save points
    DB-->>Loyalty: Updated balance
    Loyalty-->>API: Earn result
    API-->>POS: Response
25. Cấu trúc repository đề xuất
    loyalty-crm-poc/
    │
    ├── README.md
    │
    ├── docs/
    │   ├── 01-business-analysis/
    │   ├── 02-brd/
    │   ├── 03-srs/
    │   ├── 04-architecture/
    │   ├── 05-database/
    │   ├── 06-api/
    │   ├── 07-diagrams/
    │   ├── 08-wireframe/
    │   ├── 09-test-case/
    │   ├── 10-risk/
    │   ├── 11-assumptions/
    │   ├── 12-decision-log/
    │   └── 13-presentation/
    │
    ├── src/
    │
    ├── tests/
    │
    ├── scripts/
    │
    ├── postman/
    │
    └── docker-compose.yml
26. Definition of Done
    Một chức năng POC được coi là hoàn thành khi:

Có business rule.

Có FR tương ứng.

Có API specification nếu là API.

Có database model nếu cần.

Có implementation.

Có validation.

Có exception handling.

Có test.

Có documentation.

Có thể demo.

27. Claude Code Instructions
    Nguyên tắc bắt buộc
    Claude Code phải đọc README.md này trước khi triển khai.

Trước khi code:

Phân tích yêu cầu.

Xác định assumption.

Xác định business rules.

Xác định FR liên quan.

Xác định entity/database.

Xác định API contract.

Sau đó mới implementation.

Không được
Tự ý mở rộng scope.

Tự ý thay đổi business rule.

Hard-code dữ liệu nghiệp vụ quan trọng.

Bỏ qua validation.

Bỏ qua exception case.

Bỏ qua idempotency cho transaction.

Xây dựng microservice chỉ để làm kiến trúc phức tạp hơn.

Xóa hoặc sửa tài liệu hiện có mà không kiểm tra impact.

Sao chép nguyên trạng một sản phẩm/source code bên ngoài.

Khi có assumption mới
Phải cập nhật:

docs/11-assumptions/assumptions.md
Khi có quyết định quan trọng
Phải cập nhật:

docs/12-decision-log/decision-log.md
Khi thêm/chỉnh sửa requirement
Phải cập nhật:

docs/03-srs/srs.md
Khi thay đổi API
Phải cập nhật:

docs/06-api/api-specification.md
Khi thay đổi database
Phải cập nhật:

docs/05-database/
Khi hoàn thành chức năng
Kiểm tra:

Requirement
↓
Business Rule
↓
Implementation
↓
Test
↓
Documentation
28. Thứ tự triển khai đề xuất
    Claude Code nên thực hiện theo thứ tự:

Phase 1 - Analysis
Business Analysis
↓
BRD
↓
Assumptions
↓
Business Rules
Phase 2 - Requirements
SRS
↓
FR / NFR
↓
MVP prioritization
Phase 3 - Design
Architecture
↓
ERD
↓
API
↓
Sequence / Activity / DFD
Phase 4 - Prototype
Wireframe
Phase 5 - POC
Project setup
↓
Database
↓
Customer
↓
Transaction
↓
Loyalty
↓
Earn Point
↓
Tier
↓
Exception handling
Phase 6 - Testing
Unit Test
↓
Integration Test
↓
API Test
↓
10+ Test Cases
Phase 7 - Documentation
Risk
Assumption
Decision Log
Presentation
Final README
29. Tiêu chí demo POC
    Demo tối thiểu nên thực hiện được flow:

1. Create Customer
   ↓
2. Customer receives Loyalty Account
   ↓
3. POS sends Purchase Transaction
   ↓
4. System validates transaction
   ↓
5. System identifies Customer Tier
   ↓
6. System calculates earned points
   ↓
7. System updates Point Balance
   ↓
8. System stores Point Transaction
   ↓
9. POS receives response
   ↓
10. Retry same transaction
    ↓
11. System does NOT double earn points
    Đây là core demo flow và cần được ưu tiên cao nhất.

30. Nguyên tắc thiết kế quan trọng
    Consistency
    Point balance và point transaction phải nhất quán.

Idempotency
Một transaction chỉ được earn point một lần.

Auditability
Có thể truy vết vì sao balance thay đổi.

Extensibility
Business rule về Tier/Point nên có khả năng thay đổi mà không phải sửa quá nhiều code.

Simplicity
POC phải đơn giản, dễ hiểu, dễ chạy và dễ demo.

Traceability
Có thể trace:

Business Requirement
↓
Functional Requirement
↓
Use Case
↓
API
↓
Implementation
↓
Test Case
31. Final Checklist
    Business
    Business Analysis hoàn thành

Actor xác định

Main flow xác định

Exception flow xác định

Business rules xác định

Requirements
BRD

SRS

FR IDs

NFR

Priority

Design
Architecture

ERD

API Specification

Use Case

Sequence

Activity

DFD

UI
3+ Wireframes

POC
Source code chạy được

Database

API

Earn Point

Tier

Idempotency

Exception handling

Testing
10+ test cases

Happy path

Exception path

Duplicate/retry case

Documentation
Risk Assessment

Assumption Document

Decision Log >= 5

Presentation <= 20 slides

32. Kết luận
    Mục tiêu của repository không phải xây dựng một hệ thống Loyalty/CRM hoàn chỉnh mà là chứng minh năng lực từ phân tích nghiệp vụ → yêu cầu → thiết kế → POC → kiểm thử → tài liệu hóa.

Ưu tiên chất lượng của core Loyalty flow, đặc biệt:

POS Transaction
↓
Customer
↓
Tier
↓
Earn Point
↓
Point Balance
↓
Point Transaction
↓
Idempotency
Mọi quyết định ngoài phạm vi hoặc khác với các giả định trong README phải được ghi nhận trong Assumption Document hoặc Decision Log.
