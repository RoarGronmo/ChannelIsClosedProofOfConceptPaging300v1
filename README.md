# ChannelIsClosedProofOfConceptPaging300v1
ProofOfConcept

This is simply a test project for Paging 3.00, was primary made for ProofOfConcept on problems like "Channels Closed".

Contains primary a lookup of nearby petrol stations from Famapp's FuelPump station list, prepped for location lat +61.89N, lon +6.67E. Your distance to theese stations will be calcaulated if you turn on the Enable GPS switch upper right.

Paging 3.0.0-alpha03 is used with following caveats:

* Note 1: The app will crash first time you run it, this because the feedback from Paging 3.0.0-alpha03 contains some flaws.
* Note 2: The app will may run on 2.nd attempt, and show you a petrol station list (if you have access to internet), with some locaion to where they are.
* Note 3: The 2.nd attempt may crash if you scroll down (if there is mismatch between paging and the room db internals).
* Note 4: Room station list has a key to the end to its StationEntity data class, which is the paging of the data load.

* Note 5: You need access to location/GPS to make the backend data changes to happen to the room db. Tip in emulator: Make a GPS track in the emulator to emulate a movement.

* Note 6: Access to the Famapp DB from this testuser will be working for a while, it may be closed on no notice if suspicius activity is observed.

* Note 7: If you want to subsequently test `Note 1:` error, please rise the version number in Room DB (room->db->AppDataBase.kt), this will clear the Room DB, since it has fallback to destructive mode.

* Note 8: If you change the paging dependency to `alpha02`, it will run much more stable, and it will not crash on first run anymore.
