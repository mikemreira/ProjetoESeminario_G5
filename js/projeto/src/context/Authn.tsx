import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'

type ContextType = {
    user: string | undefined,
    setUser: (v: string | undefined) => void,
    avatar: string | undefined,
    setAvatar: (v: string | undefined) => void,
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    setUser: () => { },
    avatar: undefined,
    setAvatar: () => { },
})

// @ts-ignore
const getTokenFromCookie = () => document.cookie.split('; ').find(row => row.startsWith('token='))==undefined ? document.cookie.split('; ').find(row => row.startsWith('token=')) : document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1]+"="
// @ts-ignore
const getAvatarFromCookie = () => document.cookie.split('; ').find(row => row.startsWith('avatar='))==undefined ? document.cookie.split('; ').find(row => row.startsWith('avatar=')) : document.cookie.split('; ').find(row => row.startsWith('avatar=')).split('=')[1]+"="
const getAvatarFromSession = () => localStorage.getItem('avatar')

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [user, setUserState] = useState(getTokenFromCookie())
    const [avatar, setAvatar] = useState(getAvatarFromSession() || getAvatarFromCookie())

    const setUser = (user: string | undefined, avatar?: string) => {
        setUserState(user)
        setAvatar(avatar)
    }

    return (
        <LoggedInContext.Provider value={{ user: user, setUser: setUser, avatar: avatar, setAvatar: setAvatar}}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useCurrentUser() {
    return useContext(LoggedInContext).user
}

export function useSetUser() {
    return useContext(LoggedInContext).setUser
}

export function useAvatar() {
    return useContext(LoggedInContext).avatar
}

export function useSetAvatar() {
    return useContext(LoggedInContext).setAvatar
}