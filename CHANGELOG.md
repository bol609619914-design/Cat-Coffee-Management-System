# Changelog

All notable changes to this project are documented in this file.

## [2026-04-21] Phase 1 Delivery

### Added

- Added file upload support for cat avatars and drink images.
- Added a storage abstraction layer with a local storage implementation, making it easier to switch to OSS, MinIO, or other object storage providers later.
- Added static resource mapping for uploaded files so uploaded images can be previewed directly in the UI.
- Added user ownership fields for reservations and orders, enabling user-scoped data isolation.
- Added automatic schema upgrade logic for `reservation.user_id` and `customer_order.user_id` to smooth local database upgrades.
- Added ECharts-based visual analytics to the dashboard, including 7-day revenue trend and order status distribution.
- Added login page brand logo and favicon integration.
- Added a project-level `CHANGELOG.md` for structured release tracking.

### Changed

- Refined the login page visual identity with the Cat Cafe logo, brand subtitle, and improved login/register presentation.
- Reworked the sidebar brand area to use the custom Cat Cafe logo instead of the previous text-only block.
- Updated sidebar interaction states so active and hover states share a consistent rounded visual style.
- Moved `修改密码` into the bottom action zone above `退出登录` for a cleaner sidebar information hierarchy.
- Updated customer-facing labels so normal users see `我的预约` and `我的订单` instead of management-oriented wording.
- Adjusted reservation and order forms for normal users to hide unnecessary back-office fields such as status-management inputs.
- Unified several button and layout interactions across the frontend for a more coherent visual language.

### Fixed

- Fixed image upload preview failures caused by incorrect static resource mapping for uploaded files.
- Fixed upload request handling to avoid unstable multipart header behavior in the frontend.
- Fixed permission-scoped reservation and order queries so normal users only see and operate on their own records.
- Fixed role-based data presentation issues by ensuring seeded sample data is bound to the normal user account where appropriate.
- Fixed sidebar UX inconsistencies where hover and selected menu items used mismatched shapes.

### Notes

- Uploaded files are runtime assets and are now excluded from Git tracking via `.gitignore`.
- The current local storage implementation is suitable for development, demos, course projects, and graduation project delivery. For production deployment, the storage interface can be switched to a dedicated object storage provider.
