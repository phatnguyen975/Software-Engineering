<div align="center">
  <h1>Software Design</h1>
  <sub>November 25, 2025</sub>
</div>

This chapter focuses on the stage of the software engineering process where an executable software system is developed. While design and implementation are often interleaved activities, this chapter distinguishes between the creative activity of identifying software components (design) and the process of realizing the design as a program (implementation). The chapter emphasizes object-oriented design using the UML, design patterns, and critical implementation issues such as reuse and open source development.

## Object-Oriented Design Using the UML

Object-oriented (OO) design processes involve designing object classes and the relationships between them. An OO system consists of interacting objects that maintain their own local state and provide operations on that state. The chapter outlines a general process for OO design, illustrated by the design of a wilderness weather station system.

The process includes five main activities:

**1. Understand and Define the Context and Interactions**

- The first stage is understanding the relationships between the software and its external environment.
- **System Context Models:** These are structural models (often block diagrams) that demonstrate the other systems in the environment. For the weather station, this includes the weather information system, the satellite system, and the control system.
- **Interaction Models:** These are dynamic models showing how the system interacts with its environment. Use case models are commonly used here to represent specific interactions, such as "Report weather" or "Remote control".

**2. Design the System Architecture**

- This involves identifying the major components of the system and their interactions.
- Architectural patterns (like those from Chapter 6) can be applied.
- The weather station uses an architecture of independent subsystems (Data Collection, Communications, Power Manager, etc.) communicating via a broadcasting infrastructure.

**3. Identify the Principal Objects**

- This involves identifying the object classes in the system. Approaches include grammatical analysis of natural language descriptions, identifying tangible entities (things), and scenario-based analysis.
- In the weather station example, identified objects include tangible hardware classes (Ground thermometer, Anemometer) and logical system objects (WeatherStation, WeatherData).

**4. Develop Design Models**

- These models bridge the gap between requirements and implementation. They include **structural models** (static structure like class associations) and **dynamic models** (interactions and state changes).
- **Subsystem Models:** Show logical groupings of objects.
- **Sequence Models:** Show the sequence of object interactions for specific use cases. For example, a sequence diagram can show the message flow between the `SatComms`, `WeatherStation`, and `Commslink` objects during data collection.
- **State Machine Models:** Show how individual objects change state in response to events. This is crucial for real-time systems like the weather station to define states like `Shutdown`, `Running`, `Testing`, and `Transmitting`.

**5. Specify Interfaces**

- Designers must define the interfaces between components so that objects can be developed in parallel.
- Interfaces should specify operations but hide data representation.
- The UML uses the `<<interface>>` stereotype to define signatures for operations (e.g., `weatherReport`, `startInstrument`) without implementation details.

## Design Patterns

Design patterns are abstract descriptions of successful solutions to common software design problems. They allow designers to reuse the accumulated wisdom and experience of others. The concept was popularized by the "Gang of Four" (Gamma et al.).

- **Elements of a Pattern:** A pattern description includes a **name**, a **problem description** (when to apply it), a **solution description** (template of objects and relationships), and **consequences** (results and trade-offs).
- **Example - The Observer Pattern:**
  - **Problem:** Multiple displays of state are needed, and all must update when the state changes.
  - **Solution:** Separates the subject (data) from the observers (displays). The subject notifies observers when it changes.
  - **Benefit:** Allows data to change independently of its representation.
- **Usage:** Using patterns acts as a vocabulary for designers to communicate complex ideas efficiently.

## Implementation Issues

This section covers critical aspects of software engineering implementation that go beyond simple coding skills.

**1. Reuse**

- Most modern software is constructed by reusing existing software rather than writing code from scratch.
- **Levels of Reuse:**
  - **Abstraction level:** Reusing knowledge of successful abstractions (e.g., design patterns).
  - **Object level:** Reusing objects from libraries (e.g., JavaMail).
  - **Component level:** Reusing collections of objects or frameworks.
  - **System level:** Reusing entire application systems (COTS).
- **Costs:** Costs include the time to find and assess components, the cost of buying them, and the cost of adapting/configuring them.

**2. Configuration Management (CM)**

- CM is the process of managing a changing software system to ensure that changes by different developers do not interfere and that the correct versions are used.
- Fundamental activities include **Version Management** (tracking component versions), **System Integration** (defining what versions make up a system), and **Problem Tracking** (reporting and managing bugs).

**3. Host-Target Development**

- Software is rarely developed on the same machine on which it runs.
- **Development platform (Host):** Used to create the software (e.g., a powerful PC with an IDE).
- **Execution platform (Target):** Where the software runs (e.g., a mobile phone or embedded device).
- Simulators are often used on the host to test software intended for embedded targets.

## Open Source Development

Open source development is an approach where source code is published and volunteers are invited to participate in development.

- **Business Model:** Many companies use open source development to build a community or sell support services rather than selling the software product itself.
- **Benefits:** Open source systems (like Linux, Apache, MySQL) are often cheap, reliable, and allow bugs to be discovered and repaired quickly by the community.
- **Licensing:** Using open source code legally requires understanding the license.
  - **GPL (Reciprocal):** If you use GPL code in your software, your software must also be open source.
  - **LGPL (Lesser GPL):** Allows linking to open source libraries without forcing the using software to be open source.
  - **BSD (Non-reciprocal):** Allows code modification and inclusion in proprietary systems without requiring the new system to be open source.

## Exercises

### 1. Exercise 1

> Using the structured notation shown in Figure 7.3, specify the weather station use cases for Report status and Reconfigure. You should make reasonable assumptions about the functionality that is required here.

**Use Case: Report Status**

- **System:** Weather station
- **Use Case:** Report status
- **Actors:** Weather information system, Weather station
- **Data:** The weather station sends a status update containing the current power level (battery voltage), the status of the onboard memory (capacity used), and a health check code for each instrument (OK or Fault Detected).
- **Stimulus:** The weather information system sends a status request command via the satellite link. (Alternatively, this could be a scheduled timer event within the station).
- **Response:** The status data summary is sent to the weather information system.
- **Comments:** Status reports are typically requested daily to schedule maintenance if instruments fail or batteries run low.

**Use Case: Reconfigure**

- **System:** Weather station
- **Use Case:** Reconfigure
- **Actors:** Control System (or System Administrator), Weather station
- **Data:** The input data is a configuration package containing new transmission frequencies, updated instrument driver parameters, or a new software patch. The output is an acknowledgment message.
- **Stimulus:** The Control System establishes a secure connection and sends a "Reconfigure" command followed by the configuration data.
- **Response:** The station verifies the data, applies the changes (possibly requiring a restart), and sends a "Configuration Successful" or "Configuration Failed" message.
- **Comments:** This is a critical function; security checks (authentication) must be performed before accepting reconfiguration commands to prevent malicious tampering.

### 2. Exercise 2

> Assume that the MHC-PMS is being developed using an object-oriented approach. Draw a use case diagram showing at least six possible use cases for this system.

**Actors:** Medical Receptionist, Nurse, Doctor, System Administrator.

**Use Cases (inside the system boundary):**

1. **Register Patient:** (Actor: Medical Receptionist)
2. **Unregister Patient:** (Actor: Medical Receptionist)
3. **View Patient Record:** (Actors: Doctor, Nurse)
4. **Edit Patient Record:** (Actor: Doctor)
5. **Prescribe Medication:** (Actor: Doctor)
6. **Record Consultation Notes:** (Actor: Doctor, Nurse)
7. **Generate Management Report:** (Actor: System Administrator)

### 3. Exercise 3

> Using the UML graphical notation for object classes, design the following object classes, identifying attributes and operations. Use your own experience to decide on the attributes and operations that should be associated with these objects.
>
> - a telephone
> - a printer for a personal computer
> - a personal stereo system
> - a bank account
> - a library catalog

**Telephone**

- **Attributes:** `phoneNumber`, `status` (idle, ringing, connected)
- **Operations:** `dial(number)`, `answer()`, `hangUp()`, `ring()`

**Printer**

- **Attributes:** `modelName`, `inkLevel`, `paperStatus`, `queueSize`
- **Operations:** `print(document)`, `cancelJob()`, `getStatus()`, `reset()`

**Personal Stereo System**

- **Attributes:** `volumeLevel`, `currentTrack`, `playStatus` (playing, paused, stopped), `equalizerSettings`
- **Operations:** `play()`, `pause()`, `stop()`, `nextTrack()`, `setVolume(level)`

**Bank Account**

- **Attributes:** `accountNumber`, `holderName`, `balance`, `overdraftLimit`
- **Operations:** `deposit(amount)`, `withdraw(amount)`, `getBalance()`, `transfer(amount, targetAccount)`

**Library Catalog**

- **Attributes:** `totalBooks`, `booksCheckedOut`
- **Operations:** `searchByTitle(string)`, `searchByAuthor(string)`, `addBook(Book)`, `removeBook(Book)`, `checkAvailability(BookID)`

### 4. Exercise 4

> Using the weather station objects identified in Figure 7.6 as a starting point, identify further objects that may be used in this system. Design an inheritance hierarchy for the objects that you have identified.

**Further Objects:**

- **Instrument (Superclass):** A general class for all sensors.
  - **Attributes:** `ID`, `status`, `sampleRate`.
  - **Operations:** `test()`, `selfCalibrate()`.
- **RainGauge:** (Subclass of Instrument). Measures rainfall.
- **HumiditySensor:** (Subclass of Instrument). Measures humidity.
- **CommunicationChannel (Superclass):** For different comms methods.
  - **SatelliteTransceiver:** (Subclass).
  - **RadioTransceiver:** (Subclass for local maintenance).
- **PowerSource (Superclass):**
  - **SolarPanel:** (Subclass).
  - **Battery:** (Subclass).

**Inheritance Hierarchy:**

- **Instrument (Base)**
  - **Anemometer** (Inherits `ID`, `status`; Adds `windSpeed`, `windDirection`)
  - **Barometer** (Inherits `ID`, `status`; Adds `pressure`)
  - **GroundThermometer** (Inherits `ID`, `status`; Adds `temperature`)
  - **RainGauge** (Inherits `ID`, `status`; Adds `rainfallAmount`)

### 5. Exercise 5

> Develop the design of the weather station to show the interaction between the data collection subsystem and the instruments that collect weather data. Use sequence diagrams to show this interaction.

**Participants:** `:DataCollector`, `:Instrument` (this could be specific instances like `anemometer`), `:Transmitter`.

**Sequence:**

- `:DataCollector` sends message `requestValue()` to `:Instrument`.
- `:Instrument` executes internal `readSensor()` logic.
- `:Instrument` returns `value` to `:DataCollector`.
- (**Loop** this for all instruments).
- `:DataCollector` sends `formatMessage(values)` to self.
- `:DataCollector` sends `transmit(data)` to `:Transmitter`.
- `:Transmitter` returns `status` (success/fail).

### 6. Exercise 6

> Identify possible objects in the following systems and develop an object-oriented design for them. You may make any reasonable assumptions about the systems when deriving the design.
>
> - A group diary and time management system is intended to support the timetabling of meetings and appointments across a group of co-workers. When an appointment is to be made that involves a number of people, the system finds a common slot in each of their diaries and arranges the appointment for that time. If no common slots are available, it interacts with the user to rearrange his or her personal diary to make room for the appointment.
> - A filling station (gas station) is to be set up for fully automated operation. Drivers swipe their credit card through a reader connected to the pump; the card is verified by communication with a credit company computer, and a fuel limit is established. The driver may then take the fuel required. When fuel delivery is complete and the pump hose is returned to its holster, the driver’s credit card account is debited with the cost of the fuel taken. The credit card is returned after debiting. If the card is invalid, the pump returns it before fuel is dispensed.

**1. Group Diary System**

- **Objects:**
  - `Diary`: Holds a list of Appointments.
  - `Appointment`: Has date, time, duration, description, location.
  - `User`: Has a name, ID, and owns a Diary.
  - `Group`: A collection of Users.
  - `MeetingScheduler`: Logic to find common slots.
- **Design (Associations):**
  - `User` owns 1 `Diary`.
  - `Diary` contains 0..\* `Appointments`.
  - `MeetingScheduler` accesses 1..\* `Diaries`.

**2. Filling Station (Gas Station)**

- **Objects:**
  - `Pump`: Attributes (pumpID, fuelType, pricePerLiter), Operations (start, stop, dispense).
  - `CardReader`: Operations (readCard, ejectCard).
  - `Transaction`: Attributes (date, amount, fuelVolume, cardDetails).
  - `CreditSystem`: Interface to external bank.
  - `Display`: Shows cost/volume to user.
  - `Tank`: Tracks fuel inventory.
- **Design (Associations):**
  - `Pump` has 1 `CardReader` and 1 `Display`.
  - `Pump` draws from 1 `Tank`.
  - `CardReader` creates `Transaction`.
  - `Transaction` verified by `CreditSystem`.

### 7. Exercise 7

> Draw a sequence diagram showing the interactions of objects in a group diary system when a group of people are arranging a meeting.

**Participants:** `Organizer:User`, `Scheduler:MeetingScheduler`, `Participant:User`, `Diary`.

**Sequence:**

- `Organizer` sends `planMeeting(participants, duration)` to `Scheduler`.
- **Loop** (for each Participant):
  - `Scheduler` sends `getAvailability(dateRange)` to `Participant`'s `Diary`.
  - `Diary` returns freeSlots.
- `Scheduler` calculates `commonSlots`.
- `Scheduler` returns `commonSlots` to `Organizer`.
- `Organizer` selects `chosenSlot` and sends `confirmMeeting(chosenSlot)` to `Scheduler`.
- **Loop** (for each Participant):
  - `Scheduler` sends `addAppointment(meetingDetails)` to `Participant`'s `Diary`.
- `Scheduler` returns `confirmation` to `Organizer`.

### 8. Exercise 8

> Draw a UML state diagram showing the possible state changes in either the group diary or the filling station system.

**Filling Station Pump State Diagram:**

1. **Idle:** (Initial state). Screen displays "Insert Card".
2. **Validating Card:** Triggered by `CardInserted`. Actions: Contact bank.

- Transition to `Idle` if `CardInvalid`.

3. **Ready:** Triggered by `CardAuthorized`. Action: Enable pump nozzle.
4. **Pumping:** Triggered by `NozzleLifted` and `TriggerPressed`. Actions: Dispense fuel, Update Display.

- Self-transition `FuelDispensed`.

5. **Stopped:** Triggered by `TriggerReleased` or `TankFull` or `LimitReached`.

- Transition back to `Pumping` if `TriggerPressed` again (within timeout).

6. **Finalizing:** Triggered by `NozzleReplaced`. Actions: Print receipt, Debit Account.
7. Transition back to **Idle**.

### 9. Exercise 9

> Using examples, explain why configuration management is important when a team of people are developing a software product.

Configuration management (CM) is essential for teams because:

1. **Interference Prevention:** Without CM, if Developer A and Developer B both edit the same file (e.g., `SearchAlgorithm.java`) simultaneously, the last person to save overwrites the other's work. CM systems (like Git or Subversion) detect these conflicts and force a merge.
2. **Version Tracking:** If a new bug appears in the "stable" version of a product, the team needs to know exactly which changes were made recently. CM allows them to roll back to a previous version (e.g., Version 1.2) to restore functionality while fixing Version 1.3.
3. **System Building:** A software product consists of hundreds of files. CM ensures that when the system is built (compiled/linked), the correct versions of all components are used. For example, ensuring the `Billing` module is using the compatible `TaxCalculator` library version.

### 10. Exercise 10

> A small company has developed a specialized product that it configures specially for each customer. New customers usually have specific requirements to be incorporated into their system, and they pay for these to be developed. The company has an opportunity to bid for a new contract, which would more than double its customer base. The new customer also wishes to have some involvement in the configuration of the system. Explain why, in these circumstances, it might be a good idea for the company owning the software to make it open source.

1. **Reduced Maintenance Burden:** By open-sourcing, the company allows the new customer (and potentially others) to contribute to the development and maintenance. If the customer finds a bug or needs a specific configuration change, they can fix/implement it themselves rather than relying entirely on the original small team.
2. **Trust and Transparency:** The new customer wants involvement. Open source provides total transparency, assuring the customer that the software won't disappear if the small company goes bust (escrow effect) and allowing them to audit the code for quality or security.
3. **Faster Customization:** The customer can directly configure and adapt the code to their specific needs without the bottleneck of submitting change requests to the original developer.
4. **Market Expansion:** As mentioned in the chapter, this shifts the business model from selling the software to selling support and consultancy. The open-source product might attract more users who wouldn't buy a proprietary black-box system, eventually leading to more support contracts for the small company.
