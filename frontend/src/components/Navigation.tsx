import React from 'react';
import { Button } from '@mui/material';
import '../css/Navigation.css';



export const Navigation = (props: any) => {
    return (
        <div className="">
        <nav>
            <Button variant="contained" onClick={props.logout}>
                Log Out
            </Button>
        </nav>
        </div>
    );
};