{
    :name "XOR"

    :alphabet #{0 1}

    :states #{:s1 :s2 :s3}   

    :start-state :s1

    :accepting-states #{:s2}

    :transitions #{
        [:s1 [0] :s1]
        [:s1 [1] :s2]
        [:s2 [0] :s2]
        [:s2 [1] :s3]
        [:s3 [0 1] :s3]
    }
}
