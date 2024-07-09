import * as React from 'react'
import {Checkmark} from 'react-checkmark'

export default function Success() {
    return (
        <>
            <div>
                <Checkmark size='xLarge'/>
                <h2>Successfully logged in, check your cookies for token</h2>
            </div>
        </>
    )
}