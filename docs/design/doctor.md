## task1

## Add a doctor with fields

- Full name (required)
- Specialty (required)
- License number (required)
- Department (required)

## Requirement

The hospital keeps a doctor and can find that same doctor later.

## validation Rules

- Full name is required. After trimming extra spaces, it is between 2 and 160 characters.
- Specialty is required and is between 1 and 80 characters.
- License number is required and is between 1 and 40 characters.
- Two doctors cannot share the same license number. Comparison ignores letter case and extra spaces.
- Department is required and is between 1 and 80 characters.

## uniqueness rules

- Licence number should be unique for every doctor
- A second doctor with the same license number is refused, and the first record is unchanged.
- unique ID is generated for reference after registering the doctor

## API sketch

- path- /api/v1/doctors

200- doctor registered
400- Validation failed
401- Missing or invalid token
404- Doctor not found
409- Licence number conflict

## task2

## view a doctor by id

A signed-in person needs to open one doctor and read the directory record.

## Information

The view shows :

- Full name
- Specialty
- License number
- Department

## Who can do this

A signed-in person

- Admin
- Doctor
- Patient

## Requirement

Anyone who is signed in can open a single doctor that is already on file and read the directory record.

## Information

The view shows the full name, specialty, license number, and department saved for that doctor.

## Rules

- The doctor must already exist.
- An unknown doctor is refused, and the reason says the doctor was not found.
- Opening a doctor does not change the record.

## API sketch

- path- /api/v1/doctors

200- doctor found
403- Is unauthorized
404- Doctor not found
