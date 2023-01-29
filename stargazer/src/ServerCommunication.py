
'''
    generate 3 digit sessionId

    start wifi thread
    once connected to wifi, start main function in servercommunication in a thread

    in the main function:
        create a socket
        connect to www.stargazer.ninja:5000

        send the sessionId
            if response is success continue

                now start moving servos in correspondence to sessinonId
                first digit = sideways
                second digit = up down
                third digit = on off

            otherwise
                end socket
                retry previous steps

        wait for new message
            if message starts with New Session Id or something
            update session id

        from now on all messages have to have sessionId=384762346 at the end
        check calibration and orientation
            while bad:
                send message to server

        once good
            await instructions and handle each one


'''