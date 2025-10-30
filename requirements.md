The following is a Requirement Document detailing the scope, functional requirements, and non-functional specifications for the Family Medication Reminder and Launcher Application.

***

# Requirement Document: Family Medication and Communication Launcher App

## 1. Introduction and Goals

### 1.1 Purpose
The purpose of this document is to define the requirements for a dedicated application designed primarily to manage medication reminders and tracking for family members (parents). The application will also serve as a simplified launcher, offering quick access to essential communication methods.

### 1.2 Primary Goals
1.  To provide timely, visible, and audible reminders for medication intake.
2.  To accurately record and track individual medication consumption (e.g., clearly recording whether the **father is taking the medicine or whether the mother is taking the medicine or not**).
3.  To accommodate specific tracking requirements for insulin administration, including glucose monitoring and dosage suggestion.
4.  To ensure ease of use for the target audience (parents) through a clean, accessible interface.

## 2. Functional Requirements (FR)

### FR 2.1 User Management and Medication Setup

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **FR 2.1.1** | The application must allow the selection of the individual user (e.g., father or mother) when adding medicine [1, Conversation History]. |
| **FR 2.1.2** | The system must allow the user to **upload the medicine picture** during the medication addition process. |
| **FR 2.1.3** | The application must accommodate two primary types of medication: **tablets** and **insulin**. |
| **FR 2.1.4** | The system must clearly **record everything** related to medication intake (who took it and whether it was taken or not). |

### FR 2.2 Medication Alert and Reminder System

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **FR 2.2.1** | The application must utilize the **Alarm Manager service** to send medication alerts. |
| **FR 2.2.2** | When a medication time is reached, an alert must be displayed **full screen**. |
| **FR 2.2.3** | The full-screen alert must include the **medicine image and details**. |
| **FR 2.2.4** | The alert must provide interactive options to mark the medicine as **taken or not**. |
| **FR 2.2.5** | The alert must **make some sound** to notify the user. |

### FR 2.3 Snooze and Pending Logic

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **FR 2.3.1** | The initial alert will **wait for 1 minute** for a user response. |
| **FR 2.3.2** | If the medicine is not marked as taken, the alert will **snooze for 10 minutes** and prompt the user again. |
| **FR 2.3.3** | The system must repeat the snooze process (FR 2.3.2) **three times in half an hour**. |
| **FR 2.3.4** | If no response is recorded within the half-hour period, the medicine will be automatically marked as **pending**. |
| **FR 2.3.5** | The user must be allowed to manually **snooze for another 30 minutes** if they are busy and wish to take the medicine later. |

### FR 2.4 Insulin Management and Tracking

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **FR 2.4.1** | When the reminder is for insulin, the app must **ask the user to take the glucose test**. |
| **FR 2.4.2** | The user must be able to **add the glucose readings** after taking the test. |
| **FR 2.4.3** | Based on the inputted glucose readings, the application must **suggest how much insulin should be given**. |
| **FR 2.4.4** | The system must track and display insulin levels (and intake) in **daily, weekly, or monthly** views. |

### FR 2.5 Dashboard and Communication Features (Launcher Functionality)

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **FR 2.5.1** | The dashboard must display interactive options, including the current **date and time with a nice background**. |
| **FR 2.5.2** | The dashboard must prominently display **missed medicines**. |
| **FR 2.5.3** | The app must include several **bookmarks** to initiate communication. |
| **FR 2.5.4** | The bookmarks must support making **phone calls or making WhatsApp calls**. |

## 3. Non-Functional Requirements (NFR)

### NFR 3.1 Usability and UI/UX

| ID | Requirement Description | Source |
| :--- | :--- | :--- |
| **NFR 3.1.1** | The application must be designed for **tablet purpose**. |
| **NFR 3.1.2** | The UI must look **clean**. |
| **NFR 3.1.3** | The **font should be little bigger compared to regular** to ensure readability for the parents. |
| **NFR 3.1.4** | The uploaded **medicine picture** must be viewable on the alert screen to help users identify the correct medicine. |

### NFR 3.2 Constraints
The application is intended to serve as the primary interface (launcher app function) on a dedicated phone/tablet [Conversation History].